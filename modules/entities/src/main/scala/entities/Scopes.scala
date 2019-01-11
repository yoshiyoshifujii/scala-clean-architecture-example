package entities

import cats.data.NonEmptyList
import cats.syntax.list._

case class Scopes(value: NonEmptyList[Scope]) {
  val toStringList: List[String]      = value.map(_.entryName).toList
  val toSeparatedSpacesString: String = toStringList.mkString(" ")
}

object Scopes {

  def fromOptSeqString(value: Option[Seq[String]]): Option[Scopes] =
    value.map(fromSeqString)

  def fromSeqString(value: Seq[String]): Scopes =
    value
      .map(Scope.withName)
      .toList
      .toNel
      .map(v => new Scopes(v))
      .getOrElse(
        throw new IllegalArgumentException("Cannot create NonEmptyList from empty list")
      )

  def fromNELString(value: NonEmptyList[String]): Scopes =
    Scopes(value.map(Scope.withName))

}
