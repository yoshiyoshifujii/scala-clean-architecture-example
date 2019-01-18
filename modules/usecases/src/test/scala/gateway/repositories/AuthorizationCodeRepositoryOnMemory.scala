package gateway.repositories
import cats.MonadError
import entities.authorizationcode.{ AuthorizationCode, AuthorizationCodeValue }

class AuthorizationCodeRepositoryOnMemory[M[_]](implicit ME: MonadError[M, Throwable])
    extends AuthorizationCodeRepository[M] {

  private val map: scala.collection.mutable.Map[AuthorizationCodeValue, AuthorizationCode] =
    scala.collection.mutable.Map.empty

  override def hardDelete(id: AuthorizationCodeValue): M[Long] =
    ME.pure(map.remove(id).map(_ => 1L).getOrElse(0L))

  override def store(aggregate: AuthorizationCode): M[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(1L)
  }

  override def resolveById(id: AuthorizationCodeValue): M[AuthorizationCode] =
    map.get(id).map(ME.pure).getOrElse(ME.raiseError(new RuntimeException("Not Found.")))
}
