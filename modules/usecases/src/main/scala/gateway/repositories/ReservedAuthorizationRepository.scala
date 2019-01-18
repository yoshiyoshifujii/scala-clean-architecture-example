package gateway.repositories
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleReader, AggregateSingleWriter }
import entities.reservedauthorization.{ ReservedAuthorization, ReservedAuthorizationId }

trait ReservedAuthorizationRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateSingleWriter[M]
    with AggregateSingleHardDeletable[M] {
  override type IdType        = ReservedAuthorizationId
  override type AggregateType = ReservedAuthorization
}
