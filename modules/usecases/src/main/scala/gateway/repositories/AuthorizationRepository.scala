package gateway.repositories

import com.github.j5ik2o.dddbase._
import entities.authorization.{ Authorization, AuthorizationId }
import entities.token.RefreshToken

trait AuthorizationRepository[F[_]]
    extends AggregateSingleReader[F]
    with AggregateSingleWriter[F]
    with AggregateAllReader[F]
    with AggregateMultiReader[F]
    with AggregateMultiWriter[F]
    with AggregateSingleSoftDeletable[F]
    with AggregateMultiSoftDeletable[F] {
  override type IdType        = AuthorizationId
  override type AggregateType = Authorization

  def resolveByRefreshToken(token: RefreshToken): F[AggregateType]

}
