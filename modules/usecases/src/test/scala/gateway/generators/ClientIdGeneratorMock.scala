package gateway.generators

import cats.MonadError
import entities.client.ClientId

class ClientIdGeneratorMock[F[_]](implicit ME: MonadError[F, Throwable]) extends IdGenerator[F, ClientId] {
  private var count = 0L
  override def generateId: F[ClientId] = {
    count = count + 1L
    ME.pure(ClientId(count))
  }
}
