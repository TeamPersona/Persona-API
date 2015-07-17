package persona.api.account.personal

import javax.inject.Inject

import persona.util.ParseException
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

class JsonDataSchemaParser @Inject() extends DataSchemaParser {

  implicit val fieldDescriptorJsonReader: Reads[FieldDescriptor] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "isRequired").read[Boolean] and
      (JsPath \ "type").read[String]
    )(FieldDescriptor.apply _)

  implicit val dataSchemaJsonReader: Reads[DataSchema] = (
      (JsPath \ "category").read[String] and
      (JsPath \ "subcategory").read[String] and
      (JsPath \ "fields").read[Seq[FieldDescriptor]]
    )(DataSchema.apply _)

  def parse(value: String): Try[DataSchema] = {
    Try {
      Json.parse(value).validate[DataSchema] match {
        case s: JsSuccess[DataSchema] => s.get
        case e: JsError => throw new ParseException("Could not convert json to data schema")
      }
    }
  }

}
