package entities.client

import java.time.ZonedDateTime

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits._
import com.github.j5ik2o.dddbase.Aggregate
import entities._
import entities.redirecturi.RedirectUri
import entities.refkey.RefKeyGenerator
import entities.reservedauthorization.{ ReservedAuthorization, ReservedAuthorizationId }
import entities.responsetype.ResponseType
import entities.scope.Scopes
import entities.secret.Secret
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
                            state: Option[String]): ValidationResult[ReservedAuthorization] =
    (validateResponseType(responseType), validateRedirectUri(redirectUri), validateScope(scope), validateState(state)) mapN {
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

  private def validateResponseType(responseType: String): ValidationResult[ResponseType] =
    if (responseType == ResponseType.Code.entryName)
      ResponseType.Code.validNel
    else
      EntitiesError("response type is only 'code'").invalidNel

  private def validateRedirectUri(redirectUri: Option[String]): ValidationResult[RedirectUri] =
    redirectUri.map(RedirectUri).getOrElse(RedirectUri(this.redirectUris.head)).validNel

  private def validateScope(scope: Option[Seq[String]]): ValidationResult[Scopes] =
    Scopes.fromOptSeqString(scope).map {
      case Some(s) => s
      case None    => this.scopes
    }

  private def validateState(state: Option[String]): ValidationResult[Option[State]] =
    state.map(State).validNel

  def authenticate(password: String): ValidationResult[Client] =
    validateSecret(password) map { _ =>
      this
    }

  private def validateSecret(secret: String): ValidationResult[Secret] =
    if (secret == this.secret.value)
      this.secret.validNel
    else
      EntitiesError("").invalidNel

}

object Client {

  def create[M[_]: Monad](
      id: M[ClientId],
      name: Option[String],
      secret: Secret,
      redirectUris: Seq[String],
      scopes: Seq[String]
  ): M[ValidationResult[Client]] =
    for {
      _id <- id
    } yield
      (validateName(name), validateRedirectUris(redirectUris), validateScopes(scopes)) mapN {
        case (_clientName, _redirectUris, _scopes) =>
          Client(
            id = _id,
            name = _clientName,
            secret,
            redirectUris = _redirectUris,
            scopes = _scopes,
            status = Status.Active,
            createdAt = ZonedDateTime.now,
            updatedAt = None
          )
      }

  private def validateName(arg: Option[String]): ValidationResult[Option[ClientName]] =
    if (arg.exists(_.length <= 50)) arg.map(ClientName).validNel
    else EntitiesError("name fields maximum length from 50 characters").invalidNel

  private def validateRedirectUris(arg: Seq[String]): ValidationResult[NonEmptyList[String]] =
    if (arg.nonEmpty) NonEmptyList.of(arg.head, arg.tail: _*).validNel
    else EntitiesError("redirectUris fields is empty").invalidNel

  private def validateScopes(arg: Seq[String]): ValidationResult[Scopes] =
    Scopes.fromSeqString(arg)
}
