package usecases.authorization

import cats.implicits._
import entities.client.ClientId
import gateway.repositories.{ ClientRepository, ReservedAuthorizationRepository }
import usecases._

case class AuthorizationAuthorizeInput(responseType: String,
                                       clientId: Long,
                                       redirectUri: Option[String],
                                       scope: Option[Seq[String]],
                                       state: Option[String])

case class AuthorizationAuthorizeOutput(redirectUri: Option[String],
                                        scope: Option[Seq[String]],
                                        state: Option[String],
                                        refKey: Option[String],
                                        clientId: Long,
                                        clientName: Option[String])

class AuthorizationAuthorizeUseCase[M[_]](
    override protected val outputBoundary: OutputBoundary[M, AuthorizationAuthorizeOutput],
    private val clientRepository: ClientRepository[M],
    private val reservedAuthorizationRepository: ReservedAuthorizationRepository[M]
)(implicit ME: UseCaseMonadError[M])
    extends UseCaseInteractor[M, AuthorizationAuthorizeInput, AuthorizationAuthorizeOutput] {

  override protected def dance(arg: AuthorizationAuthorizeInput): M[AuthorizationAuthorizeOutput] =
    for {
      clientId <- ME.pure(ClientId(arg.clientId))
      client   <- clientRepository.resolveById(clientId)
      reservedAuth <- client
        .reservedAuthorization(
          responseType = arg.responseType,
          redirectUri = arg.redirectUri,
          scope = arg.scope,
          state = arg.state
        ).toM[M]
      _ <- reservedAuthorizationRepository.store(reservedAuth)
    } yield
      AuthorizationAuthorizeOutput(
        redirectUri = Some(reservedAuth.redirectUri.value),
        scope = Some(reservedAuth.scopes.toStringList),
        state = reservedAuth.state.map(_.value),
        refKey = Some(reservedAuth.id.value),
        clientId = client.id.value,
        clientName = client.name.map(_.value)
      )
}
