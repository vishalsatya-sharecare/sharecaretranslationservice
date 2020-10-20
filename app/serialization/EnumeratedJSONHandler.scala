package serialization

import enumeratum.EnumEntry
import play.api.libs.json._

trait EnumeratedJSONHandler[E <: EnumEntry] extends Reads[E] with Writes[E] {
  def lookup(s: String): E
  override def reads(json: JsValue): JsResult[E] = json match {
    case JsString(s) => JsSuccess(lookup(s))
    case _           => JsError("error.expected.jsstring")
  }
  override def writes(o: E): JsValue = JsString(o.entryName)
}