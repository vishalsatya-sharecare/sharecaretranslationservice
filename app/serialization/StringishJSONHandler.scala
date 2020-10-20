package serialization

import play.api.libs.json._

trait StringishJSONHandler[A] extends Reads[A] with Writes[A] {
  def to(a: A): String
  def from(s: String): A

  override def reads(json: JsValue): JsResult[A] = json match {
    case JsString(s) => JsSuccess(from(s))
    case _           => JsError("error.expected.jsstring")
  }
  override def writes(o: A): JsValue = JsString(to(o))

}