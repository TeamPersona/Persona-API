package persona.api.account.personal

import javax.inject.Inject

import persona.util.{BadFormatError, ParseError, ValidationError}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try
import scalaz.Scalaz._
import scalaz.ValidationNel

class JsonDataSchemaParser @Inject() extends DataSchemaParser {

  private implicit val fieldDescriptorJsonReader: Reads[FieldDescriptor] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "isRequired").read[Boolean] and
      (JsPath \ "type").read[String]
    )(FieldDescriptor.apply _)

  private implicit val dataSchemaJsonReader: Reads[DataSchema] = (
      (JsPath \ "category").read[String] and
      (JsPath \ "subcategory").read[String] and
      (JsPath \ "fields").read[Seq[FieldDescriptor]]
    )(DataSchema.apply _)

  def parse(value: String): ValidationNel[ParseError, DataSchema] = {
    val maybeJson = Try(Json.parse(value))

    maybeJson map { json =>
      json.validate[DataSchema] match {
        case s: JsSuccess[DataSchema] => s.get.successNel
        case e: JsError =>
          val errorsAsJson = JsError.toJson(e)
          val errorsAsString = Json.stringify(errorsAsJson)

          new ValidationError(errorsAsString).failureNel
      }
    } getOrElse {
      new BadFormatError(value).failureNel
    }
  }

}
