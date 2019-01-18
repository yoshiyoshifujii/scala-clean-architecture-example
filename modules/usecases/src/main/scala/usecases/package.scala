import cats.MonadError
import cats.data.NonEmptyList
import entities.{ EntitiesError, EntitiesValidationResult }

package object usecases {

  sealed trait UseCaseError
  case class UseCaseSystemError(cause: Throwable)     extends UseCaseError
  case class UseCaseApplicationError(message: String) extends UseCaseError
  object UseCaseApplicationError {
    def apply(message: NonEmptyList[EntitiesError]): UseCaseApplicationError =
      new UseCaseApplicationError(message.toString())
  }

  type UseCaseMonadError[F[_]] = MonadError[F, UseCaseError]

  implicit class EntitiesError2MonadError[A](val v: EntitiesValidationResult[A]) extends AnyVal {
    def toM[F[_]](implicit ME: UseCaseMonadError[F]): F[A] =
      v.fold(
        ne => ME.raiseError(UseCaseApplicationError(ne)),
        ME.pure
      )
  }

}
