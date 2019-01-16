package entities

import cats.data.NonEmptyList
import cats.implicits._

case class Scopes(value: NonEmptyList[Scope]) {
  val toStringList: List[String]      = value.map(_.entryName).toList
  val toSeparatedSpacesString: String = toStringList.mkString(" ")
}

object Scopes {

  def fromOptSeqString(value: Option[Seq[String]]): ValidationResult[Option[Scopes]] =
    value match {
      case Some(v) => fromSeqString(v).map(_.some)
      case None    => None.validNel
    }

  def fromSeqString(value: Seq[String]): ValidationResult[Scopes] =
    value
      .map(Scope.withName)
      .toList
      .toNel
      .map(new Scopes(_).validNel)
      .getOrElse(
        EntitiesError("Cannot create NonEmptyList from empty list").invalidNel
      )

  def fromNELString(value: NonEmptyList[String]): Scopes =
    Scopes(value.map(Scope.withName))

}
