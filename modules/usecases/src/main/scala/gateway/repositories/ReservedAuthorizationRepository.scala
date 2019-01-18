package gateway.repositories
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleReader, AggregateSingleWriter }
import entities.reservedauthorization.{ ReservedAuthorization, ReservedAuthorizationId }

trait ReservedAuthorizationRepository[F[_]]
    extends AggregateSingleReader[F]
    with AggregateSingleWriter[F]
    with AggregateSingleHardDeletable[F] {
  override type IdType        = ReservedAuthorizationId
  override type AggregateType = ReservedAuthorization
}
