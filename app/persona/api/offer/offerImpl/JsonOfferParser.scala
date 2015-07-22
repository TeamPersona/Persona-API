package persona.api.offer.offerImpl

import java.util.UUID

import org.joda.time.DateTime
import persona.api.offer.Offer

import persona.util.ParseException
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

class JsonOfferParser {
  private implicit val offerCriterionJsonReader: Reads[OfferCriterionDescriptor] = (
    (JsPath \ "criterionCategory").read[String] and
      (JsPath \ "criterion").read[String]
    )(OfferCriterionDescriptor.apply _)

  private implicit val offerJsonReader: Reads[OfferSchema] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "creationDay").read[DateTime] and
      (JsPath \ "description").read[String] and
      (JsPath \ "expirationTime").read[DateTime] and
      (JsPath \ "currentParticipants").read[Int] and
      (JsPath \ "maxParticipants").read[Int] and
      (JsPath \ "value").read[Double] and
      (JsPath \ "criteria").read[Seq[OfferCriterionDescriptor]]
    )(OfferSchema.apply _)

  def parse(value: String): Try[Offer] = {
    Try {
      Json.parse(value).validate[Offer] match {
        case s: JsSuccess[Offer] => s.get
        case e: JsError => throw new ParseException("Could not convert json to offer")
      }
    }
  }
}
