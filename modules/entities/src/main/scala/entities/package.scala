import cats.data.ValidatedNel

package object entities {

  case class EntitiesError(message: String)

  type ValidationResult[A] = ValidatedNel[EntitiesError, A]

}
