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

class AuthorizationAuthorizeUseCase[F[_]](
    override protected val outputBoundary: OutputBoundary[F, AuthorizationAuthorizeOutput],
    private val clientRepository: ClientRepository[F],
    private val reservedAuthorizationRepository: ReservedAuthorizationRepository[F]
)(implicit ME: UseCaseMonadError[F])
    extends UseCaseInteractor[F, AuthorizationAuthorizeInput, AuthorizationAuthorizeOutput] {

  override protected def dance(arg: AuthorizationAuthorizeInput): F[AuthorizationAuthorizeOutput] =
    for {
      clientId <- ME.pure(ClientId(arg.clientId))
      client   <- clientRepository.resolveById(clientId)
      reservedAuth <- client
        .reservedAuthorization(
          responseType = arg.responseType,
          redirectUri = arg.redirectUri,
          scope = arg.scope,
          state = arg.state
        ).toM[F]
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
