package usecases.admin

import cats.Monad
import cats.implicits._
import gateway.repositories.ClientRepository
import usecases.{ OutputBoundary, UseCaseInteractor }

case class ClientGetsInput()

case class ClientOutput(id: Long,
                        name: Option[String],
                        redirectUris: Seq[String],
                        scopes: Seq[String],
                        createdAt: Long,
                        updatedAt: Option[Long])

case class ClientGetsOutput(clients: Seq[ClientOutput])

final class ClientGetsUseCase[F[_]: Monad](
    override protected val outputBoundary: OutputBoundary[F, ClientGetsOutput],
    private val clientRepository: ClientRepository[F]
) extends UseCaseInteractor[F, ClientGetsInput, ClientGetsOutput] {

  override protected def dance(arg: ClientGetsInput): F[ClientGetsOutput] =
    clientRepository.resolveAll.map { aggregates =>
      ClientGetsOutput(aggregates.map { e =>
        ClientOutput(
          id = e.id.value,
          name = e.name.map(_.value),
          redirectUris = e.redirectUris.toList,
          scopes = e.scopes.toStringList,
          createdAt = e.createdAt.toInstant.toEpochMilli,
          updatedAt = e.updatedAt.map(_.toInstant.toEpochMilli)
        )
      })
    }

}
