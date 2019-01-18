package usecases.authorization

import cats.implicits._
import entities.authorization.AuthorizationId
import entities.reservedauthorization.ReservedAuthorizationId
import gateway.generators.IdGenerator
import gateway.repositories.{
  AuthorizationCodeRepository,
  AuthorizationRepository,
  ClientRepository,
  ReservedAuthorizationRepository
}
import usecases._

case class AuthorizationApproveInput(refKey: String, clientId: Long, scope: Option[Seq[String]], accountId: String)

case class AuthorizationApproveOutput(redirectUri: Option[String],
                                      state: Option[String],
                                      code: Option[String],
                                      accountId: Option[String])

class AuthorizationApproveUseCase[M[_]](
    override protected val outputBoundary: OutputBoundary[M, AuthorizationApproveOutput],
    private val authorizationIdGenerator: IdGenerator[M, AuthorizationId],
    private val reservedAuthorizationRepository: ReservedAuthorizationRepository[M],
    private val authorizationCodeRepository: AuthorizationCodeRepository[M],
    private val authorizationRepository: AuthorizationRepository[M]
)(implicit ME: UseCaseMonadError[M])
    extends UseCaseInteractor[M, AuthorizationApproveInput, AuthorizationApproveOutput] {

  override protected def dance(arg: AuthorizationApproveInput): M[AuthorizationApproveOutput] =
    for {
      reservedAuthId  <- ME.pure(ReservedAuthorizationId(arg.refKey))
      reservedAuth    <- reservedAuthorizationRepository.resolveById(reservedAuthId)
      authorizationId <- authorizationIdGenerator.generateId
      authTuple       <- reservedAuth.approve(authorizationId, arg.clientId, arg.scope, arg.accountId).toM[M]
      authCode        <- authorizationCodeRepository.store(authTuple._1).map(_ => authTuple._1)
      auth            <- authorizationRepository.store(authTuple._2).map(_ => authTuple._2)
      _               <- reservedAuthorizationRepository.hardDelete(reservedAuthId)
    } yield
      AuthorizationApproveOutput(
        redirectUri = Some(authCode.redirectUri.value),
        state = reservedAuth.state.map(_.value),
        code = Some(authCode.value.value),
        accountId = Some(auth.accountId)
      )
}
