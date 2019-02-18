package gateway.repositories

import cats.MonadError
import entities.client.{ Client, ClientId }
import entities.status.Status

class ClientRepositoryOnMemory[F[_], E](implicit ME: MonadError[F, E]) extends ClientRepository[F] {

  private val map: scala.collection.mutable.Map[ClientId, Client] = scala.collection.mutable.Map.empty

  override def resolveMulti(ids: Seq[ClientId]): F[Seq[Client]] = ???

  override def store(aggregate: Client): F[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(aggregate.id.value)
  }

  override def softDeleteMulti(ids: Seq[ClientId]): F[Long] = ???

  override def resolveAll: F[Seq[Client]] =
    ME.pure(map.values.toSeq)

  override def resolveById(id: ClientId): F[Client] =
    map.get(id).map(ME.pure).get

  override def softDelete(id: ClientId): F[Long] = {
    map
      .get(id).map { client =>
        store(client.copy(status = Status.Deleted))
      }.getOrElse(ME.pure(0L))
  }

  override def storeMulti(aggregates: Seq[Client]): F[Long] = ???

}
