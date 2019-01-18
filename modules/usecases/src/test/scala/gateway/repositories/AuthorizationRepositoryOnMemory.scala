package gateway.repositories
import cats.MonadError
import entities.authorization.{ Authorization, AuthorizationId }
import entities.status.Status
import entities.token.RefreshToken

class AuthorizationRepositoryOnMemory[F[_]](implicit ME: MonadError[F, Throwable]) extends AuthorizationRepository[F] {

  private val map: scala.collection.mutable.Map[AuthorizationId, Authorization] = scala.collection.mutable.Map.empty

  override def resolveByRefreshToken(token: RefreshToken): F[Authorization] = ???

  override def softDelete(id: AuthorizationId): F[Long] =
    map
      .get(id).map { client =>
        store(client.copy(status = Status.Deleted))
      }.getOrElse(ME.pure(0L))

  override def resolveById(id: AuthorizationId): F[Authorization] =
    map.get(id).map(ME.pure).getOrElse(ME.raiseError(new RuntimeException("Not Found.")))

  override def softDeleteMulti(ids: Seq[AuthorizationId]): F[Long] = ???

  override def storeMulti(aggregates: Seq[Authorization]): F[Long] = ???

  override def resolveAll: F[Seq[Authorization]] =
    ME.pure(map.values.toSeq)
  override def store(aggregate: Authorization): F[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(aggregate.id.value)
  }

  override def resolveMulti(ids: Seq[AuthorizationId]): F[Seq[Authorization]] = ???
}
