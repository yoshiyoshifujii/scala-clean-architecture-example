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

  type UseCaseMonadError[M[_]] = MonadError[M, UseCaseError]

  implicit class EntitiesError2MonadError[A](val v: EntitiesValidationResult[A]) extends AnyVal {
    def toM[M[_]](implicit ME: UseCaseMonadError[M]): M[A] =
      v.fold(
        ne => ME.raiseError(UseCaseApplicationError(ne)),
        ME.pure
      )
  }

}
