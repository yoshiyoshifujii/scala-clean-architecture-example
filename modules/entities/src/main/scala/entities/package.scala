import cats.data.ValidatedNel

package object entities {

  case class EntitiesError(message: String)

  type ValidationResult[A] = ValidatedNel[EntitiesError, A]

  trait EnumWithValidation[E <: enumeratum.EnumEntry] {
    self: enumeratum.Enum[E] =>
    import cats.implicits._

    def withNameValidation(name: String): ValidationResult[E] =
      self.withNameOption(name).map(_.validNel).getOrElse(EntitiesError(s"$name is not a member").invalidNel)
  }

}
