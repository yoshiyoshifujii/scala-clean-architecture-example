package gateway.repositories
import cats.MonadError
import entities.authorizationcode.{ AuthorizationCode, AuthorizationCodeValue }

class AuthorizationCodeRepositoryOnMemory[F[_], E](implicit ME: MonadError[F, E])
    extends AuthorizationCodeRepository[F] {

  private val map: scala.collection.mutable.Map[AuthorizationCodeValue, AuthorizationCode] =
    scala.collection.mutable.Map.empty

  override def hardDelete(id: AuthorizationCodeValue): F[Long] =
    ME.pure(map.remove(id).map(_ => 1L).getOrElse(0L))

  override def store(aggregate: AuthorizationCode): F[Long] = {
    map.update(aggregate.id, aggregate)
    ME.pure(1L)
  }

  override def resolveById(id: AuthorizationCodeValue): F[AuthorizationCode] =
    map.get(id).map(ME.pure).get
}
