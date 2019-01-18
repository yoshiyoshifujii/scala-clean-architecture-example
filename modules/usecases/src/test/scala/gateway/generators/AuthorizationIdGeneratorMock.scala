package gateway.generators

import cats.MonadError
import entities.authorization.AuthorizationId

class AuthorizationIdGeneratorMock[F[_]](implicit ME: MonadError[F, Throwable])
    extends IdGenerator[F, AuthorizationId] {
  private var count = 0L
  override def generateId: F[AuthorizationId] = {
    count = count + 1L
    ME.pure(AuthorizationId(count))
  }
}
