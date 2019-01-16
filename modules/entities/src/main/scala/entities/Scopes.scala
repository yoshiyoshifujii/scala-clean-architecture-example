package entities

import cats.data.NonEmptyList
import cats.data.Validated.{ Invalid, Valid }
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
    value.toList.toNel
      .map(fromNELString)
      .getOrElse(
        EntitiesError("scopes fields is empty").invalidNel
      )

  def fromNELString(value: NonEmptyList[String]): ValidationResult[Scopes] =
    value
      .map(Scope.withNameValidation).foldLeft(Seq.empty[Scope].validNel[EntitiesError]) {
        case (Valid(acc), Valid(v))           => (acc :+ v).validNel
        case (invalid @ Invalid(_), Valid(_)) => invalid
        case (Valid(_), invalid @ Invalid(_)) => invalid
        case (Invalid(e1), Invalid(e2))       => Invalid(e1 ++ e2.toList)
      }.map { a =>
        Scopes(NonEmptyList.of(a.head, a.tail: _*))
      }

}
