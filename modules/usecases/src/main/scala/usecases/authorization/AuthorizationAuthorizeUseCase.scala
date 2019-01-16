package usecases.authorization

import cats.MonadError
import cats.implicits._
import entities.EntitiesValidationResult
import entities.client.ClientId
import gateway.repositories.ClientRepository
import usecases.{ OutputBoundary, UseCaseInteractor }

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
    private val clientRepository: ClientRepository[M]
)(implicit ME: MonadError[M, Throwable])
    extends UseCaseInteractor[M, AuthorizationAuthorizeInput, AuthorizationAuthorizeOutput] {

  override protected def call(
      arg: AuthorizationAuthorizeInput
  ): M[EntitiesValidationResult[AuthorizationAuthorizeOutput]] = ???
//    for {
//      clientId <- ME.point(ClientId(arg.clientId))
//      client   <- clientRepository.resolveById(clientId)
//      reservedAuth <- client
//        .reservedAuthorization(
//          responseType = arg.responseType,
//          redirectUri = arg.redirectUri,
//          scope = arg.scope,
//          state = arg.state
//        )
//      _ <- reservedAuthorizationRepository.store(reservedAuth).run(redisConnection)
//    } yield
//      AuthorizationAuthorizeOutput(
//        redirectUri = Some(reservedAuth.redirectUri.value),
//        scope = Some(reservedAuth.scopes.toStringList),
//        state = reservedAuth.state.map(_.value),
//        refKey = Some(reservedAuth.id.value),
//        clientId = client.id.value,
//        clientName = client.name
//      )
}
