package gateway.repositories

import com.github.j5ik2o.dddbase._
import entities.{ Client, ClientId }

trait ClientRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateSingleWriter[M]
    with AggregateAllReader[M]
    with AggregateMultiReader[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M]
    with AggregateMultiSoftDeletable[M] {
  override type IdType        = ClientId
  override type AggregateType = Client
}

object ClientRepository {
  def apply[M[_]](implicit M: ClientRepository[M]): ClientRepository[M] = M
}
