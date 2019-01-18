package gateway.repositories

import com.github.j5ik2o.dddbase._
import entities.client.{ Client, ClientId }

trait ClientRepository[F[_]]
    extends AggregateSingleReader[F]
    with AggregateSingleWriter[F]
    with AggregateAllReader[F]
    with AggregateMultiReader[F]
    with AggregateMultiWriter[F]
    with AggregateSingleSoftDeletable[F]
    with AggregateMultiSoftDeletable[F] {
  override type IdType        = ClientId
  override type AggregateType = Client
}
