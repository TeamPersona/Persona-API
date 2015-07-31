package persona.controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import persona.api.authentication.User
import persona.api.offer.OfferService
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import persona.api.offer.offerImpl._

import scala.concurrent.Future

@Singleton
class Offers @Inject() (offerService: OfferService,
                         jsonOfferWriter: JsonOfferWriter) extends Controller {

  def list = Action.async {
    val offers = offerService.list
      offers map {offer =>
        val json = jsonOfferWriter.toJson(offer)
        Ok(json)
      }
  }

  def get(id: UUID) = Action.async {

    offerService.get(id).map { optionOffer =>
      optionOffer map { offer =>
        val json = jsonOfferWriter.toJson(offer)
        Ok(json)
      } getOrElse {
        Ok("Could not find the offer: " + id)
      }
    }
  }




  def participate(id: UUID) = Action.async {
    // First, retrieve the offer
    val retrieveOffer = offerService.get(id)

    retrieveOffer flatMap { maybeOffer =>
      // Check if we found the offer
      maybeOffer map { offer =>
        // Now try participating in the offer
        val participateInOffer = offer.participate(new User(UUID.randomUUID()))

        participateInOffer map { maybeResult =>
          // Check if we successfully joined the offer
          maybeResult map { _ =>
            // Everything went OK, return updated details about the offer
            Redirect(routes.Offers.get(id))
          } getOrElse {
            // Something went wrong, return an error
            InternalServerError
          }
        }
      } getOrElse {
        // Didn't find the offer, return an error
        Future.successful(InternalServerError)
      }
    }
  }
}
