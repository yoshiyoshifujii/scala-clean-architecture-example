package usecases.admin

import cats.implicits._
import entities.client.{ Client, ClientId }
import entities.client.secret.SecretGenerator
import gateway.generators.IdGenerator
import gateway.repositories.ClientRepository
import usecases._

case class ClientCreateInput(name: Option[String], redirectUris: Seq[String], scopes: Seq[String])

case class ClientCreateOutput(id: Long, secret: String)

final class ClientCreateUseCase[F[_]](
    override protected val outputBoundary: OutputBoundary[F, ClientCreateOutput],
    private val clientIdGenerator: IdGenerator[F, ClientId],
    private val clientRepository: ClientRepository[F]
)(implicit ME: UseCaseMonadError[F])
    extends UseCaseInteractor[F, ClientCreateInput, ClientCreateOutput] {

  override protected def dance(arg: ClientCreateInput): F[ClientCreateOutput] =
    for {
      id <- clientIdGenerator.generateId
      client <- Client
        .create(
          id,
          name = arg.name,
          secret = SecretGenerator.generate,
          redirectUris = arg.redirectUris,
          scopes = arg.scopes
        ).toM[F]
      _ <- clientRepository.store(client)
    } yield
      ClientCreateOutput(
        id = client.id.value,
        secret = client.secret.value
      )

}
