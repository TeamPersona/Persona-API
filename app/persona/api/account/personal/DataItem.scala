package persona.api.account.personal

import org.joda.time.DateTime

case class DataItem (
    creationTime: DateTime,
    category: String,
    subcategory: String,
    data: Map[String, String])
