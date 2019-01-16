package usecases.admin

import cats.MonadError
import cats.implicits._
import entities.EntitiesValidationResult
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

final class ClientGetsUseCase[M[_]](
    override protected val outputBoundary: OutputBoundary[M, ClientGetsOutput],
    private val clientRepository: ClientRepository[M]
)(implicit ME: MonadError[M, Throwable])
    extends UseCaseInteractor[M, ClientGetsInput, ClientGetsOutput] {

  override protected def call(arg: ClientGetsInput): M[EntitiesValidationResult[ClientGetsOutput]] =
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
      }).validNel
    }

}
