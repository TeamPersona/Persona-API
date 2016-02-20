package com.persona.service.offer

import spray.json.DefaultJsonProtocol

case class OfferFilter (val filterCategory: String, val filters: List[OfferCriterionDescriptor]) {

}

trait OfferFilterJsonProtocol extends DefaultJsonProtocol with CriterionDescriptorJsonProtocol {

  implicit val offerFilterJsonParser = jsonFormat2(OfferFilter)

}