package gateway.repositories

import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleReader, AggregateSingleWriter }
import entities.authorizationcode.{ AuthorizationCode, AuthorizationCodeValue }

trait AuthorizationCodeRepository[F[_]]
    extends AggregateSingleReader[F]
    with AggregateSingleWriter[F]
    with AggregateSingleHardDeletable[F] {
  override type IdType        = AuthorizationCodeValue
  override type AggregateType = AuthorizationCode
}
