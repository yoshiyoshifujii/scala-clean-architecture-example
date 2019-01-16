package entities.authorizationcode

import com.github.j5ik2o.dddbase.AggregateStringId

case class AuthorizationCodeValue(value: String) extends AggregateStringId
