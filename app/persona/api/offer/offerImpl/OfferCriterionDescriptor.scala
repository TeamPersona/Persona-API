package persona.api.offer.offerImpl

// Just a class used to parse JSON criteria
// will add in validation that it's valid once we know more about what's going on
class OfferCriterionDescriptor private (val criterionCategory: String, criterion: String){

}

object OfferCriterionDescriptor {
  def apply(criterionCategory: String, criterion: String): OfferCriterionDescriptor = {
    new OfferCriterionDescriptor(criterionCategory, criterion)
  }
}
