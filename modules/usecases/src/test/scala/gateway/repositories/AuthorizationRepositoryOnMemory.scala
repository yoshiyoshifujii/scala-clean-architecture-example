package gateway.repositories
import cats.MonadError
import entities.authorization.{ Authorization, AuthorizationId }
import entities.status.Status
import entities.token.RefreshToken

class AuthorizationRepositoryOnMemory[M[_]](implicit ME: MonadError[M, Throwable]) extends AuthorizationRepository[M] {

  private val map: scala.collection.mutable.Map[AuthorizationId, Authorization] = scala.collection.mutable.Map.empty

  override def resolveByRefreshToken(token: RefreshToken): M[Authorization] = ???

  override def softDelete(id: AuthorizationId): M[Long] =
    map
      .get(id).map { client =>
        store(client.copy(status = Status.Deleted))
      }.getOrElse(ME.pure(0L))

  override def resolveById(id: AuthorizationId): M[Authorization] =
    map.get(id).map(ME.pure).getOrElse(ME.raiseError(new RuntimeException("Not Found.")))

  override def softDeleteMulti(ids: Seq[AuthorizationId]): M[Long] = ???

  override def storeMulti(aggregates: Seq[Authorization]): M[Long] = ???

  override def resolveAll: M[Seq[Authorization]] =
    ME.pure(map.values.toSeq)
  override def store(aggregate: Authorization): M[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(aggregate.id.value)
  }

  override def resolveMulti(ids: Seq[AuthorizationId]): M[Seq[Authorization]] = ???
}
