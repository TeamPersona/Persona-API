package persona.controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import persona.api.authentication.User
import persona.api.offer.OfferService
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class Offers @Inject() (offerService: OfferService) extends Controller {

  def list = Action.async {
    offerService.list map { option =>
      option map { offers =>
        Ok(JsString("Listing offers!"))
      } getOrElse {
        InternalServerError
      }
    }
  }

  def get(id: Long) = Action.async {
    offerService.get(id) map { option =>
      option map { offer =>
        Ok(JsString("Listing offer " + offer.id))
      } getOrElse {
        InternalServerError
      }
    }
  }

  def participate(id: Long) = Action.async {
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
