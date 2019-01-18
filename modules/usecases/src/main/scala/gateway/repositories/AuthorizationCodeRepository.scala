package gateway.repositories

import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleReader, AggregateSingleWriter }
import entities.authorizationcode.{ AuthorizationCode, AuthorizationCodeValue }

trait AuthorizationCodeRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateSingleWriter[M]
    with AggregateSingleHardDeletable[M] {
  override type IdType        = AuthorizationCodeValue
  override type AggregateType = AuthorizationCode
}
