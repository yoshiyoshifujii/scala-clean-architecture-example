package usecases.admin

import cats.implicits._
import entities.client.{ Client, ClientId }
import entities.client.secret.SecretGenerator
import gateway.generators.IdGenerator
import gateway.repositories.ClientRepository
import usecases._

case class ClientCreateInput(name: Option[String], redirectUris: Seq[String], scopes: Seq[String])

case class ClientCreateOutput(id: Long, secret: String)

final class ClientCreateUseCase[M[_]](
    override protected val outputBoundary: OutputBoundary[M, ClientCreateOutput],
    private val clientIdGenerator: IdGenerator[M, ClientId],
    private val clientRepository: ClientRepository[M]
)(implicit ME: UseCaseMonadError[M])
    extends UseCaseInteractor[M, ClientCreateInput, ClientCreateOutput] {

  override protected def dance(arg: ClientCreateInput): M[ClientCreateOutput] =
    for {
      id <- clientIdGenerator.generateId
      client <- Client
        .create(
          id,
          name = arg.name,
          secret = SecretGenerator.generate,
          redirectUris = arg.redirectUris,
          scopes = arg.scopes
        ).toM[M]
      _ <- clientRepository.store(client)
    } yield
      ClientCreateOutput(
        id = client.id.value,
        secret = client.secret.value
      )

}
