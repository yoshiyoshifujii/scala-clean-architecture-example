package entities.authorizationcode

import cats.implicits._
import com.github.j5ik2o.dddbase.Aggregate
import entities.{ EntitiesError, EntitiesValidationResult }
import entities.authorization.AuthorizationId
import entities.client.ClientId
import entities.redirecturi.RedirectUri
import entities.status.Status

import scala.reflect.{ classTag, ClassTag }

case class AuthorizationCode(id: AuthorizationCodeValue,
                             status: Status,
                             authorizationId: AuthorizationId,
                             redirectUri: RedirectUri,
                             clientId: ClientId)
    extends Aggregate {
  val value: AuthorizationCodeValue = this.id
  override type AggregateType = AuthorizationCode
  override type IdType        = AuthorizationCodeValue
  override protected val tag: ClassTag[AuthorizationCode] = classTag[AuthorizationCode]

  def authenticate(redirectUri: Option[String], clientId: ClientId): EntitiesValidationResult[AuthorizationCode] =
    (assertRedirectUri(redirectUri), assertClientId(clientId)) mapN {
      case (_, _) => this
    }

  private def assertRedirectUri(redirectUri: Option[String]): EntitiesValidationResult[RedirectUri] =
    (redirectUri, this.redirectUri) match {
      case (Some(a), b) if a == b.value => this.redirectUri.validNel
      case (Some(a), b) if a != b.value => EntitiesError("un match redirectUri").invalidNel
      case (None, _)                    => EntitiesError("required redirectUri").invalidNel
    }

  private def assertClientId(clientId: ClientId): EntitiesValidationResult[ClientId] =
    if (clientId == this.clientId)
      clientId.validNel
    else
      EntitiesError("un match clientId").invalidNel
}
