package gateway.generators

import cats.MonadError
import entities.authorization.AuthorizationId

class AuthorizationIdGeneratorMock[M[_]](implicit ME: MonadError[M, Throwable])
    extends IdGenerator[M, AuthorizationId] {
  private var count = 0L
  override def generateId: M[AuthorizationId] = {
    count = count + 1L
    ME.pure(AuthorizationId(count))
  }
}
