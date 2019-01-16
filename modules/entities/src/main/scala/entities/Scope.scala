package entities

import cats.implicits._
import enumeratum._

import scala.collection.immutable

abstract sealed class Scope(override val entryName: String) extends EnumEntry

object Scope extends Enum[Scope] {
  override def values: immutable.IndexedSeq[Scope] = findValues
  case object ReadOnly  extends Scope("read-only")
  case object ReadWrite extends Scope("read-write")

  def withNameValidation(name: String): ValidationResult[Scope] =
    super.withNameOption(name).map(_.validNel).getOrElse(EntitiesError(s"$name is not a member").invalidNel)
}
