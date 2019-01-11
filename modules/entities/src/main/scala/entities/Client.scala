package entities

import java.time.ZonedDateTime

import cats.data.NonEmptyList
import com.github.j5ik2o.dddbase.Aggregate

import scala.reflect.{ classTag, ClassTag }

case class Client(id: ClientId,
                  status: Status,
                  name: Option[String],
                  secret: Secret,
                  redirectUris: NonEmptyList[String],
                  scopes: Scopes,
                  createdAt: ZonedDateTime,
                  updatedAt: Option[ZonedDateTime])
    extends Aggregate {
  override type AggregateType = Client
  override type IdType        = ClientId
  override protected val tag: ClassTag[Client] = classTag[Client]
}

object Client {

  def create[M[_]](
      id: M[ClientId],
      status: Status,
      name: Option[String],
      secret: Secret,
      redirectUris: NonEmptyList[String],
      scopes: Scopes,
      createdAt: ZonedDateTime,
      updatedAt: Option[ZonedDateTime]
  ): M[Client] =

}
