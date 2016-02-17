package com.persona.service.bank

import com.persona.util.json.DateTimeJsonProtocol
import org.joda.time.DateTime
import spray.json._

case class DataItem(
  creationTime: DateTime,
  category: String,
  subcategory: String,
  data: Map[String, String])

case class RawDataItem(
  category: String,
  subcategory: String,
  data: Map[String, String]) {

  def process(): DataItem = {
    DataItem(
      DateTime.now,
      category,
      subcategory,
      data
    )
  }

}

trait DataItemJsonProtocol extends DefaultJsonProtocol with DateTimeJsonProtocol {

  implicit val rawDataItemJsonParser = jsonFormat3(RawDataItem)
  implicit val dataItemJsonParser = jsonFormat4(DataItem)

}
