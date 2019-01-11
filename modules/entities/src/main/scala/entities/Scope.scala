package entities

import enumeratum._

import scala.collection.immutable

abstract sealed class Scope(override val entryName: String) extends EnumEntry

object Scope extends Enum[Scope] {
  override def values: immutable.IndexedSeq[Scope] = findValues
  case object ReadOnly  extends Scope("read-only")
  case object ReadWrite extends Scope("read-write")
}
