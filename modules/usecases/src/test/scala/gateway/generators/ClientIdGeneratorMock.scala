package gateway.generators

import cats.MonadError
import entities.ClientId

class ClientIdGeneratorMock[M[_]](implicit ME: MonadError[M, Throwable]) extends IdGenerator[M, ClientId] {
  private var count = 0L
  override def generateId: M[ClientId] = {
    count = count + 1L
    ME.point(ClientId(count))
  }
}