package entities.token

import entities.EnumWithValidation
import enumeratum._

import scala.collection.immutable

sealed abstract class TokenType(override val entryName: String) extends EnumEntry

object TokenType extends Enum[TokenType] with EnumWithValidation[TokenType] {
  override def values: immutable.IndexedSeq[TokenType] = findValues
  case object Bearer extends TokenType("Bearer")
}
