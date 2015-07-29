package persona.api.account.personal

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

class JsonDataItemWriter {

  private implicit val dataItemJsonWriter = (
      (JsPath \ "owner").write[UUID] and
      (JsPath \ "creationTime").write[DateTime] and
      (JsPath \ "category").write[String] and
      (JsPath \ "subcategory").write[String] and
      (JsPath \ "data").write[Map[String, String]]
    )(unlift(DataItem.unapply))

  def toJson(dataItem: DataItem): JsValue = Json.toJson(dataItem)

  def toJson(dataItems: Seq[DataItem]): JsValue = Json.toJson(dataItems)

}
