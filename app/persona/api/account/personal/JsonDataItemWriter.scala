package persona.api.account.personal

import play.api.libs.json._

class JsonDataItemWriter {

  private implicit val dataItemJsonWriter = new Writes[DataItem] {
    def writes(dataItem: DataItem): JsValue = {
      Json.obj(
        "creationTime" -> dataItem.creationTime,
        "category" -> dataItem.category,
        "subcategory" -> dataItem.subcategory,
        "data" -> dataItem.data
      )
    }
  }

  def toJson(dataItem: DataItem): JsValue = Json.toJson(dataItem)

  def toJson(dataItems: Seq[DataItem]): JsValue = Json.toJson(dataItems)

}
