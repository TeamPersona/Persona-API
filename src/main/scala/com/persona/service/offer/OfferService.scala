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

  object ListOffers
  case class GetOffer(id: UUID)

}

private class OfferServiceActor(offerDAO: OfferDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case OfferServiceActor.ListOffers =>
      offerDAO.list().pipeTo(sender)

    case OfferServiceActor.GetOffer(id) =>
      // TODO - Don't think we need this anymore
      val NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L
      val epoch = (id.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000
      val date = new DateTime(epoch)
      val fmt = DateTimeFormat.forPattern("YYYY-MM-dd")
      val dateStr = fmt.print(date)

      offerDAO.get(id, dateStr).pipeTo(sender)
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

  def list()(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    implicit val timeout = OfferService.ListTimeout
    val futureResult = actor ? OfferServiceActor.ListOffers

    futureResult.map { result =>
      result.asInstanceOf[Seq[Offer]]
    }
  }

  def get(id: UUID)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.GetOffer(id)

    futureResult.map { result =>
      result.asInstanceOf[Option[Offer]]
    }
  }

}
