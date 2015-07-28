package persona.api.account.personal

import java.util.UUID

import org.joda.time.DateTime

case class DataItem (
  ownerID: UUID,
  creationTime: DateTime,
  category: String,
  subcategory: String,
  data: Map[String, String])
