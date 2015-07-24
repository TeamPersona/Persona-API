package persona.api.offer.offerImpl

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import persona.api.offer.Offer

// TODO: add in checking to make sure they are valid criteria
class OfferSchema private (val creationDay: DateTime,
                            val description: String,
                            val expirationTime: DateTime,
                            val currentParticipants: Int,
                            val maxParticipants: Int,
                            val value: Double,
                            criteriaDescriptors: Seq[OfferCriterionDescriptor]) {

  def validate(offer: Offer): Boolean = {
    //TODO: make real
    true
  }
}

object OfferSchema {
  def apply(creationDay: DateTime, description: String, expirationTime: DateTime, currentParticipants: Int, maxParticipants: Int, value: Double, criteriaDescriptors: Seq[OfferCriterionDescriptor]) : OfferSchema = {
    new OfferSchema(creationDay, description, expirationTime, currentParticipants, maxParticipants, value, criteriaDescriptors)
  }
}

