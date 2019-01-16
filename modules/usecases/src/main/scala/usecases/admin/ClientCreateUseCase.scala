package usecases.admin

import cats.MonadError
import cats.implicits._
import entities._
import gateway.generators.IdGenerator
import gateway.repositories.ClientRepository
import usecases.{ OutputBoundary, UseCaseInteractor }

case class ClientCreateInput(name: Option[String], redirectUris: Seq[String], scopes: Seq[String])

case class ClientCreateOutput(id: Long, secret: String)

final class ClientCreateUseCase[M[_]](
    override protected val outputBoundary: OutputBoundary[M, ClientCreateOutput],
    private val clientIdGenerator: IdGenerator[M, ClientId],
    private val clientRepository: ClientRepository[M]
)(implicit ME: MonadError[M, Throwable])
    extends UseCaseInteractor[M, ClientCreateInput, ClientCreateOutput] {

  override protected def call(arg: ClientCreateInput): M[ValidationResult[ClientCreateOutput]] =
    for {
      client <- Client.create(
        id = clientIdGenerator.generateId,
        name = arg.name,
        secret = SecretGenerator.generate,
        redirectUris = arg.redirectUris,
        scopes = arg.scopes
      )
      _ <- client.fold(
        _ => ME.point(client),
        clientRepository.store(_).map(_ => client)
      )
    } yield
      client.map { _client =>
        ClientCreateOutput(
          id = _client.id.value,
          secret = _client.secret.value
        )
      }

}
