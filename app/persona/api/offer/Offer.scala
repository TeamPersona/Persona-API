package persona.api.offer

import org.joda.time.DateTime
import persona.api.authentication.User
import persona.api.offer.offerImpl.InvalidOfferException
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class Offer (val id: String,          // This is a timeUUID that has the exact time it was created
             creationDay: DateTime, // TODO: may have to change to string type/check to match Cassandra
             description: String,
             expirationTime: DateTime,
             currentParticipants: Int,
             maxParticipants: Int,
             value: Double,
             criteria: Map[String,String]) {

  if(criteria.isEmpty) {
    throw new InvalidOfferException("There are no criteria for the offer")
  }

  def participate(user: User): Future[Option[Unit]] = {
    Future {
      None
    }
  }

}