package persona.api.account.personal

import java.util.UUID

import org.joda.time.DateTime

case class DataItem (
  ownerID: UUID,
  creationTime: DateTime,
  category: String,
  subcategory: String,
  data: Map[String, String]) {

  if(category.isEmpty || subcategory.isEmpty) {
    throw new InvalidDataException("Data item is not correctly categorized")
  }

  if(data.isEmpty) {
    throw new InvalidDataException("Data item has no data")
  }

}
