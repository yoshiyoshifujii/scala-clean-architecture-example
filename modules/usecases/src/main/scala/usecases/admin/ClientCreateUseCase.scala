package usecases.admin

import java.time.ZonedDateTime

import cats.data.NonEmptyList
import entities._
import gateway.Context
import gateway.generators.IdGenerator
import gateway.repositories.ClientRepository
import usecases.{OutputBoundary, UseCaseInteractor}

case class ClientCreateInput(name: Option[String], redirectUris: Seq[String], scopes: Seq[String])

case class ClientCreateOutput(id: Long, secret: String)

class ClientCreateUseCase[M[_]](
    override protected val outputBoundary: OutputBoundary[M, ClientCreateOutput],
    protected val clientIdGenerator: IdGenerator[M, ClientId],
    protected val clientRepository: ClientRepository[Context.Reader]
) extends UseCaseInteractor[M, ClientCreateInput, ClientCreateOutput] {

  override protected def call(arg: ClientCreateInput): M[ClientCreateOutput] =
    for {
      id <- clientIdGenerator.generateId
      client <- Future.apply {
        Client(
          id = id,
          status = Status.Active,
          name = arg.name,
          secret = SecretGenerator.generate,
          redirectUris = NonEmptyList.of(arg.redirectUris.head, arg.redirectUris.tail: _*),
          scopes = Scopes.fromSeqString(arg.scopes),
          createdAt = ZonedDateTime.now,
          updatedAt = None
        )
      }
      a <- clientRepository.store(client).run
    } yield
      ClientCreateOutput(
        id = id.value,
        secret = client.secret.value
      )

}
