package com.persona.service.offer

import java.util.UUID

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.persona.util.actor.ActorWrapper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

private object OfferServiceActor {

  case class ListOffers(lastID: Int)
  case class GetOffer(id: Int)

}

private class OfferServiceActor(offerDAO: OfferDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case OfferServiceActor.ListOffers(lastID) =>
      offerDAO.list(lastID).pipeTo(sender)

    case OfferServiceActor.GetOffer(id) =>
      offerDAO.get(id).pipeTo(sender)
  }

}

object OfferService {

  private val ListTimeout = Timeout(60.seconds)
  private val GetTimeout = Timeout(60.seconds)

  def apply(offerDAO: OfferDAO)(implicit actorSystem: ActorSystem): OfferService = {
    val actor = actorSystem.actorOf(
      Props(
        new OfferServiceActor(offerDAO)
      )
    )

    new OfferService(actor)
  }

}

class OfferService private(actor: ActorRef) extends ActorWrapper(actor) {

  def list(lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    implicit val timeout = OfferService.ListTimeout
    val futureResult = actor ? OfferServiceActor.ListOffers(lastID)

    futureResult.map { result =>
      result.asInstanceOf[Seq[Offer]]
    }
  }

  def get(id: Int)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.GetOffer(id)

    futureResult.map { result =>
      result.asInstanceOf[Option[Offer]]
    }
  }

}
