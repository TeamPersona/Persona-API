package persona.controllers

import javax.inject.{Inject, Singleton}

import persona.api.offer.OfferService
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class Offers @Inject() (offerService: OfferService) extends Controller {

  def list = Action.async {
    Future.successful(Ok(JsString("Hello, world!")))
  }

  def get(id: Long) = Action.async {
    Future.successful(Ok(JsString("Retrieving offer " + id)))
  }

  def participate(id: Long) = Action {
    Redirect(persona.controllers.routes.Offers.get(id))
  }

}
