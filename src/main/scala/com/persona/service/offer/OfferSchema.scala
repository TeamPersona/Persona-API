package com.persona.service.offer


class OfferSchema private (val offerInfo: OfferBasicInfo,
                           val offerType: List[String],
                           val offerFilters: List[OfferFilter],
                           val offerInfoRequired: List[OfferFilter],
                           val isEligible: Boolean,
                           val isParticipating: Boolean) {



  def validate(offer: Offer): Boolean = {
    true
  }
}

object OfferSchema {

  def apply(offerInfo: OfferBasicInfo, offerType: List[String], offerFilters: List[OfferFilter], isEligible: Boolean, offerInfoRequired: List[OfferFilter], isParticipating: Boolean) : OfferSchema = {
    new OfferSchema(offerInfo, offerType, offerFilters, offerInfoRequired, isEligible, isParticipating)
  }

}