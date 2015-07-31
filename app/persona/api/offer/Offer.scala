package persona.api.offer

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

case class Offer(val id: UUID, // This is a timeUUID that has the exact time it was created
                 creationDay: DateTime, // TODO: may have to change to string type/check to match Cassandra
                 description: String,
                 expirationTime: DateTime,
                 currentParticipants: Int,
                 maxParticipants: Int,
                 value: Double,
                 criteria: Map[String, String]) {

  require(criteria != null && criteria.nonEmpty)

  def participate(): Future[Option[Unit]] = {
    Future {
      None
    }
  }

}
