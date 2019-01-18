package gateway.repositories
import cats.MonadError
import entities.reservedauthorization.{ ReservedAuthorization, ReservedAuthorizationId }

class ReservedAuthorizationRepositoryOnMemory[F[_]](implicit ME: MonadError[F, Throwable])
    extends ReservedAuthorizationRepository[F] {

  private val map: scala.collection.mutable.Map[ReservedAuthorizationId, ReservedAuthorization] =
    scala.collection.mutable.Map.empty

  override def hardDelete(id: ReservedAuthorizationId): F[Long] =
    ME.pure(map.remove(id).map(_ => 1L).getOrElse(0L))

  override def store(aggregate: ReservedAuthorization): F[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(1L)
  }

  override def resolveById(id: ReservedAuthorizationId): F[ReservedAuthorization] =
    map.get(id).map(ME.pure).getOrElse(ME.raiseError(new RuntimeException("Not Found.")))
}
