package com.persona.service.offer


class OfferSchema private (val offerInfo: OfferBasicInfo,
                           val criteria: List[OfferCriterionDescriptor],
                           val matchesFilters: Boolean) {

  //require(criteriaDescriptors != null && criteriaDescriptors.nonEmpty)
  //require(description != null && !description.isEmpty)

  //TODO: also check the DateTime and Int/Double fields


  def validate(offer: Offer): Boolean = {
    //TODO: make real, figure out how to validate offer data
    true
  }
}

object OfferSchema {

  def apply(offerInfo: OfferBasicInfo, criteria: List[OfferCriterionDescriptor], matchesFilters: Boolean) : OfferSchema = {
    new OfferSchema(offerInfo, criteria, matchesFilters)
  }

}