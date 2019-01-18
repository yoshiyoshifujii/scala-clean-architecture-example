package gateway.repositories
import cats.MonadError
import entities.reservedauthorization.{ ReservedAuthorization, ReservedAuthorizationId }

class ReservedAuthorizationRepositoryOnMemory[M[_]](implicit ME: MonadError[M, Throwable])
    extends ReservedAuthorizationRepository[M] {

  private val map: scala.collection.mutable.Map[ReservedAuthorizationId, ReservedAuthorization] =
    scala.collection.mutable.Map.empty

  override def hardDelete(id: ReservedAuthorizationId): M[Long] =
    ME.pure(map.remove(id).map(_ => 1L).getOrElse(0L))

  override def store(aggregate: ReservedAuthorization): M[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(1L)
  }

  override def resolveById(id: ReservedAuthorizationId): M[ReservedAuthorization] =
    map.get(id).map(ME.pure).getOrElse(ME.raiseError(new RuntimeException("Not Found.")))
}
