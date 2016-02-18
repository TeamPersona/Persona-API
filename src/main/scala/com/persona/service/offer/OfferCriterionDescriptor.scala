package com.persona.service.offer


import spray.json.DefaultJsonProtocol

// Just a class used to parse JSON criteria
// will add in validation that it's valid once we know more about what's going on
case class OfferCriterionDescriptor  (val criterionCategory: String, val isMissing: Boolean)

//object OfferCriterionDescriptor {
//  def apply(criterionCategory: String, isMissing: Boolean): OfferCriterionDescriptor = {
//    new OfferCriterionDescriptor(criterionCategory, isMissing)
//  }
//  def unapply(criterion: OfferCriterionDescriptor): Option[(String, Boolean)] =
//    Some((criterion.criterionCategory, criterion.isMissing))
//}

trait CriterionDescriptorJsonProtocol extends DefaultJsonProtocol {

  implicit val criterionJsonParser = jsonFormat2(OfferCriterionDescriptor)

}
