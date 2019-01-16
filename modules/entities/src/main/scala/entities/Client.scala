package entities

import java.time.ZonedDateTime

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits._
import com.github.j5ik2o.dddbase.Aggregate

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
