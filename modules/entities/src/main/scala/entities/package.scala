import cats.data.ValidatedNel

package object entities {

  case class EntitiesError(message: String)

  type EntitiesValidationResult[A] = ValidatedNel[EntitiesError, A]

  private[entities] trait EnumWithValidation[E <: enumeratum.EnumEntry] {
    self: enumeratum.Enum[E] =>
    import cats.implicits._

    def withNameValidation(name: String): EntitiesValidationResult[E] =
      self.withNameOption(name).map(_.validNel).getOrElse(EntitiesError(s"$name is not a member").invalidNel)
  }

}
