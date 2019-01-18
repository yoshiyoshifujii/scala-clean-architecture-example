package gateway.repositories

import com.github.j5ik2o.dddbase._
import entities.authorization.{ Authorization, AuthorizationId }
import entities.token.RefreshToken

trait AuthorizationRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateSingleWriter[M]
    with AggregateAllReader[M]
    with AggregateMultiReader[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M]
    with AggregateMultiSoftDeletable[M] {
  override type IdType        = AuthorizationId
  override type AggregateType = Authorization

  def resolveByRefreshToken(token: RefreshToken): M[AggregateType]

}
