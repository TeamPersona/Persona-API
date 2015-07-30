package persona.api.offer.offerImpl

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import persona.api.offer.Offer
import play.api.libs.json._


class JsonOfferWriter {

  private implicit val offerJsonWriter: Writes[Offer] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "creationDay").write[DateTime] and
      (JsPath \ "description").write[String] and
      (JsPath \ "expirationTime").write[DateTime] and
      (JsPath \ "currentParticipants").write[Int] and
      (JsPath \ "maxParticipants").write[Int] and
      (JsPath \ "value").write[Double] and
      (JsPath \ "criteria").write[Map[String, String]]
    )(unlift(Offer.unapply))

  def toJson(offer: Offer): JsValue = Json.toJson(offer)

  def toJson(offers: Seq[Offer]): JsValue = Json.toJson(offers)

}
