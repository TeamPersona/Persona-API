package persona.api.offer

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class Offer (val id: Long,
             description: String,
             creationTime: Long,
             expirationTime: Long,
             currentParticipants: Int,
             maxParticipants: Int) {

  def participate(): Future[Option[Unit]] = {
    Future {
      None
    }
  }

}
