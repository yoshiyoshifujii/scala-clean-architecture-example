package gateway.repositories

import cats.MonadError
import entities.client.{ Client, ClientId }
import entities.status.Status

class ClientRepositoryOnMemory[M[_]](implicit ME: MonadError[M, Throwable]) extends ClientRepository[M] {

  private val map: scala.collection.mutable.Map[ClientId, Client] = scala.collection.mutable.Map.empty

  override def resolveMulti(ids: Seq[ClientId]): M[Seq[Client]] = ???

  override def store(aggregate: Client): M[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(aggregate.id.value)
  }

  override def softDeleteMulti(ids: Seq[ClientId]): M[Long] = ???

  override def resolveAll: M[Seq[Client]] =
    ME.pure(map.values.toSeq)

  override def resolveById(id: ClientId): M[Client] =
    map.get(id).map(ME.pure).getOrElse(ME.raiseError(new RuntimeException("Not Found.")))

  override def softDelete(id: ClientId): M[Long] = {
    map
      .get(id).map { client =>
        store(client.copy(status = Status.Deleted))
      }.getOrElse(ME.pure(0L))
  }

  override def storeMulti(aggregates: Seq[Client]): M[Long] = ???

}
