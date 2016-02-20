package com.persona.service.offer

import spray.json._

case class Offer(val offerInfo: OfferBasicInfo,
                 val offerType: List[String],
                 val offerFilters: List[OfferFilter],
                 val offerInfoRequired: List[OfferFilter],
                 val isEligible: Boolean,
                 val isParticipating: Boolean)

trait OfferJsonProtocol extends DefaultJsonProtocol with OfferBasicInfoJsonProtocol with CriterionDescriptorJsonProtocol with OfferFilterJsonProtocol {

  implicit val offerJsonParser = jsonFormat6(Offer.apply)

}
