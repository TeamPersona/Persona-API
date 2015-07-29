package persona.api.offer.offerImpl

import org.joda.time.DateTime

import persona.api.offer.Offer

class OfferSchema private (val creationDay: DateTime,
                            val description: String,
                            val expirationTime: DateTime,
                            val currentParticipants: Int,
                            val maxParticipants: Int,
                            val value: Double,
                            criteriaDescriptors: Map[String, OfferCriterionDescriptor]) {

  if(description.isEmpty) {
    throw new InvalidOfferSchema("Offer schema has no description")
  }

  //TODO: also check the DateTime and Int/Double fields

  if(criteriaDescriptors.isEmpty) {
    throw new InvalidOfferSchema("Offer schema has no criteria")
  }

  def this(creationDay: DateTime, description: String, expirationTime: DateTime, currentParticipants: Int, maxParticipants: Int, value: Double, criteriaDescriptors: Seq[OfferCriterionDescriptor]) = {
    this(creationDay, description, expirationTime, currentParticipants, maxParticipants, value, criteriaDescriptors.map(criterion => criterion.criterionCategory -> criterion).toMap)
  }

  def validate(offer: Offer): Boolean = {
    //TODO: make real, figure out how to validate offer data
    true
  }
}

object OfferSchema {

  def apply(creationDay: DateTime, description: String, expirationTime: DateTime, currentParticipants: Int, maxParticipants: Int, value: Double, criteriaDescriptors: Seq[OfferCriterionDescriptor]) : OfferSchema = {
    new OfferSchema(creationDay, description, expirationTime, currentParticipants, maxParticipants, value, criteriaDescriptors)
  }

}
