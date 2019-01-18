package entities.reservedauthorization

import java.time.ZonedDateTime

import cats.implicits._
import com.github.j5ik2o.dddbase.Aggregate
import entities.authorization.{ Authorization, AuthorizationId }
import entities.authorizationcode.{ AuthorizationCode, AuthorizationCodeValueGenerator }
import entities.client.ClientId
import entities.redirecturi.RedirectUri
import entities.reservedauthorization.responsetype.ResponseType
import entities.scope.Scopes
import entities.state.State
import entities.status.Status
import entities.{ EntitiesError, EntitiesValidationResult }

import scala.reflect.{ classTag, ClassTag }

case class ReservedAuthorization(id: ReservedAuthorizationId,
                                 responseType: ResponseType,
                                 clientId: ClientId,
                                 redirectUri: RedirectUri,
                                 scopes: Scopes,
                                 state: Option[State],
                                 status: Status,
                                 createdAt: ZonedDateTime,
                                 updatedAt: Option[ZonedDateTime])
    extends Aggregate {
  override type AggregateType = ReservedAuthorization
  override type IdType        = ReservedAuthorizationId
  override protected val tag: ClassTag[ReservedAuthorization] = classTag[ReservedAuthorization]

  def approve(authorizationId: AuthorizationId,
              clientId: Long,
              scope: Option[Seq[String]],
              accountId: String): EntitiesValidationResult[(AuthorizationCode, Authorization)] =
    (assertClientId(clientId), assertScope(scope)) mapN {
      case (_clientId, _scopes) =>
        (
          AuthorizationCode(
            id = AuthorizationCodeValueGenerator.generate,
            status = Status.Active,
            authorizationId = authorizationId,
            redirectUri = this.redirectUri,
            clientId = _clientId
          ),
          Authorization(
            id = authorizationId,
            status = Status.Active,
            clientId = _clientId,
            scopes = _scopes,
            accountId = accountId,
            refreshToken = None,
            createdAt = ZonedDateTime.now,
            updatedAt = None
          )
        )
    }

  private def assertClientId(clientId: Long): EntitiesValidationResult[ClientId] =
    if (clientId == this.clientId.value)
      this.clientId.validNel
    else
      EntitiesError("client id is wrong.").invalidNel

  private def assertScope(scope: Option[Seq[String]]): EntitiesValidationResult[Scopes] =
    if (true)
      this.scopes.validNel
    else
      EntitiesError("scope is bad.").invalidNel

  def deny(clientId: Long): EntitiesValidationResult[ReservedAuthorization] =
    assertClientId(clientId).map(_ => this)

}
