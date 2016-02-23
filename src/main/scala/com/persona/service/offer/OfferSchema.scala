package com.persona.service.offer


class OfferSchema private (val offerInfo: OfferBasicInfo,
                           val offerType: List[String],
                           val offerFilters: Map[String, List[Map[String, String]]],
                           val offerInfoRequired: Map[String, List[Map[String, String]]],
                           val isEligible: Boolean,
                           val isParticipating: Boolean) {



  def validate(offer: Offer): Boolean = {
    true
  }
}

object OfferSchema {

  def apply(offerInfo: OfferBasicInfo, offerType: List[String], offerFilters: Map[String, List[Map[String, String]]], isEligible: Boolean, offerInfoRequired: Map[String, List[Map[String, String]]], isParticipating: Boolean) : OfferSchema = {
    new OfferSchema(offerInfo, offerType, offerFilters, offerInfoRequired, isEligible, isParticipating)
  }

}