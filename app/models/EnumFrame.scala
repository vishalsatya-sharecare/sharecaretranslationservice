package models

import serialization.EnumeratedJSONHandler
import enumeratum._

abstract class EnumFrame[E <: EnumEntry] extends Enum[E] {
  implicit val jsonHandler = new EnumeratedJSONHandler[E] {
    override def lookup(s: String): E = withName(s)
  }
}
