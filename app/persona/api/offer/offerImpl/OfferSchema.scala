package persona.api.offer.offerImpl

import java.util.UUID

import org.joda.time.DateTime

// TODO: add in checking to make sure they are valid criteria
class OfferSchema private (val id: UUID,      //TODO: need this here?
                            creationDay: DateTime,
                            description: String,
                            expirationTime: DateTime,
                            currentParticipants: Int,
                            maxParticipants: Int,
                            value: Double,
                            criteriaDescriptors: Seq[OfferCriterionDescriptor]) {

}

object OfferSchema {
  def apply(id: UUID,creationDay: DateTime, description: String, expirationTime: DateTime, currentParticipants: Int, maxParticipants: Int, value: Double, criteriaDescriptors: Seq[OfferCriterionDescriptor]) : OfferSchema = {
    new OfferSchema(UUID, creationDay, description, expirationTime, currentParticipants, maxParticipants, value, criteriaDescriptors)
  }
}

