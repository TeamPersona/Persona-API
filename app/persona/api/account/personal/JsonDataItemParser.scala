package persona.api.account.personal

import org.joda.time.DateTime
import persona.model.authentication.User
import persona.util.{ParseError, ValidationError}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scalaz.Scalaz._
import scalaz.ValidationNel

class JsonDataItemParser {

  private case class RawDataItem(category: String, subcategory: String, data: Map[String, String])

  private implicit val rawDataItemJsonReader = (
      (JsPath \ "category").read[String] and
      (JsPath \ "subcategory").read[String] and
      (JsPath \ "data").read[Map[String, String]]
    )(RawDataItem.apply _)

  def parse(user: User, value: JsValue): ValidationNel[ParseError, DataItem] = {
    val rawDataValidation = value.validate[RawDataItem] match {
      case s: JsSuccess[RawDataItem] => s.get.successNel
      case e: JsError =>
        val errorsAsJson = JsError.toJson(e)
        val errorsAsString = Json.stringify(errorsAsJson)

        new ValidationError(errorsAsString).failureNel
    }

    rawDataValidation.map { rawDataItem =>
      DataItem(
        user.userId,
        DateTime.now(),
        rawDataItem.category,
        rawDataItem.subcategory,
        rawDataItem.data
      )
    }
  }

}
