package entities.reservedauthorization.responsetype

import entities.EnumWithValidation
import enumeratum._

import scala.collection.immutable

abstract sealed class ResponseType(override val entryName: String) extends EnumEntry

object ResponseType extends Enum[ResponseType] with EnumWithValidation[ResponseType] {
  override def values: immutable.IndexedSeq[ResponseType] = findValues
  case object Code extends ResponseType("code")
}
