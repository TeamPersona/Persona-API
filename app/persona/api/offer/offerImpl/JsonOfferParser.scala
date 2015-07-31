package persona.api.offer.offerImpl

import org.joda.time.DateTime

import persona.util.{ValidationError, ParseError}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._


import scalaz.Scalaz._
import scalaz.ValidationNel

class JsonOfferParser {
  implicit val DefaultJodaDateReads = Reads.jodaDateReads("yyyy-MM-dd")

  private implicit val offerCriterionJsonReader: Reads[OfferCriterionDescriptor] = (
    (JsPath \ "criterionCategory").read[String] and
      (JsPath \ "criterion").read[String]
    )(OfferCriterionDescriptor.apply _)

  private implicit val offerJsonReader: Reads[OfferSchema] = (
      (JsPath \ "creationDay").read[DateTime] and
      (JsPath \ "description").read[String] and
      (JsPath \ "expirationTime").read[DateTime] and
      (JsPath \ "currentParticipants").read[Int] and
      (JsPath \ "maxParticipants").read[Int] and
      (JsPath \ "value").read[Double] and
      (JsPath \ "criteria").read[Seq[OfferCriterionDescriptor]]
    )(OfferSchema.apply _)

  def parse(value: String): ValidationNel[ParseError, OfferSchema] = {
      Json.parse(value).validate[OfferSchema] match {
        case s: JsSuccess[OfferSchema] => s.get.successNel
        case e: JsError =>
          val errorsAsJson = JsError.toJson(e)
          val errorsAsString = Json.stringify(errorsAsJson)

          new ValidationError(errorsAsString).failureNel
      }
  }
}
