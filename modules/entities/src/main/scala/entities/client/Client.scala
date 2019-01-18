package entities.client

import java.time.ZonedDateTime

import cats.data.NonEmptyList
import cats.implicits._
import com.github.j5ik2o.dddbase.Aggregate
import entities._
import entities.redirecturi.RedirectUri
import entities.client.refkey.RefKeyGenerator
import entities.reservedauthorization.{ ReservedAuthorization, ReservedAuthorizationId }
import entities.reservedauthorization.responsetype.ResponseType
import entities.scope.Scopes
import entities.client.secret.Secret
import entities.state.State
import entities.status.Status

import scala.reflect.{ classTag, ClassTag }

case class Client(id: ClientId,
                  name: Option[ClientName],
                  secret: Secret,
                  redirectUris: NonEmptyList[String],
                  scopes: Scopes,
                  status: Status,
                  createdAt: ZonedDateTime,
                  updatedAt: Option[ZonedDateTime])
    extends Aggregate {
  override type AggregateType = Client
  override type IdType        = ClientId
  override protected val tag: ClassTag[Client] = classTag[Client]

  def reservedAuthorization(responseType: String,
                            redirectUri: Option[String],
                            scope: Option[Seq[String]],
                            state: Option[String]): EntitiesValidationResult[ReservedAuthorization] =
    (assertResponseType(responseType), assertRedirectUri(redirectUri), assertScope(scope), assertState(state)) mapN {
      case (_responseType, _redirectUri, _scope, _state) =>
        ReservedAuthorization(
          id = ReservedAuthorizationId(RefKeyGenerator.generate.value),
          status = Status.Active,
          responseType = _responseType,
          clientId = this.id,
          redirectUri = _redirectUri,
          scopes = _scope,
          state = _state,
          createdAt = ZonedDateTime.now,
          updatedAt = None
        )
    }

  private def assertResponseType(responseType: String): EntitiesValidationResult[ResponseType] =
    if (responseType == ResponseType.Code.entryName)
      ResponseType.Code.validNel
    else
      EntitiesError("response type is only 'code'").invalidNel

  private def assertRedirectUri(redirectUri: Option[String]): EntitiesValidationResult[RedirectUri] =
    redirectUri.map(RedirectUri).getOrElse(RedirectUri(this.redirectUris.head)).validNel

  private def assertScope(scope: Option[Seq[String]]): EntitiesValidationResult[Scopes] =
    Scopes.fromOptSeqString(scope).map {
      case Some(s) => s
      case None    => this.scopes
    }

  private def assertState(state: Option[String]): EntitiesValidationResult[Option[State]] =
    state.map(State).validNel

  def authenticate(password: String): EntitiesValidationResult[Client] =
    assertSecret(password) map { _ =>
      this
    }

  private def assertSecret(secret: String): EntitiesValidationResult[Secret] =
    if (secret == this.secret.value)
      this.secret.validNel
    else
      EntitiesError("").invalidNel

}

object Client {

  def create(
      id: ClientId,
      name: Option[String],
      secret: Secret,
      redirectUris: Seq[String],
      scopes: Seq[String]
  ): EntitiesValidationResult[Client] =
    (assertName(name), assertRedirectUris(redirectUris), assertScopes(scopes)) mapN {
      case (_clientName, _redirectUris, _scopes) =>
        Client(
          id,
          name = _clientName,
          secret,
          redirectUris = _redirectUris,
          scopes = _scopes,
          status = Status.Active,
          createdAt = ZonedDateTime.now,
          updatedAt = None
        )
    }

  private def assertName(arg: Option[String]): EntitiesValidationResult[Option[ClientName]] =
    if (arg.exists(_.length <= 50)) arg.map(ClientName).validNel
    else EntitiesError("name fields maximum length from 50 characters").invalidNel

  private def assertRedirectUris(arg: Seq[String]): EntitiesValidationResult[NonEmptyList[String]] =
    if (arg.nonEmpty) NonEmptyList.of(arg.head, arg.tail: _*).validNel
    else EntitiesError("redirectUris fields is empty").invalidNel

  private def assertScopes(arg: Seq[String]): EntitiesValidationResult[Scopes] =
    Scopes.fromSeqString(arg)
}
