package com.persona.service.offer

import spray.json._

case class Offer(val offerInfo: OfferBasicInfo,
                 val criteria: List[OfferCriterionDescriptor],
                 val matchesFilters: Boolean)

trait OfferJsonProtocol extends DefaultJsonProtocol with OfferBasicInfoJsonProtocol with CriterionDescriptorJsonProtocol {

  implicit val offerJsonParser = jsonFormat3(Offer.apply)

}
