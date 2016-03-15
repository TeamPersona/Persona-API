package com.persona.service.offer

import java.util.UUID

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.persona.service.account.Account
import com.persona.util.actor.ActorWrapper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

private object OfferServiceActor {

  case class ListOffers(account: Account, lastID: Int)
  case class GetOffer(account: Account, offerid: Int)
  case class Participate(account: Account, offerid: Int)
  case class UnParticipate(account: Account, offerid: Int)
  case class getRecommended(account: Account)
  case class getPending(account: Account)
  case class getCompleted(account: Account)

}

private class OfferServiceActor(offerDAO: OfferDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case OfferServiceActor.ListOffers(account, lastID) =>
      offerDAO.list(account, lastID).pipeTo(sender)

    case OfferServiceActor.GetOffer(account, offerid) =>
      offerDAO.get(account, offerid).pipeTo(sender)

    case OfferServiceActor.Participate(account, offerid) =>
      offerDAO.participate(account, offerid).pipeTo(sender)

    case OfferServiceActor.UnParticipate(account, offerid) =>
      offerDAO.unparticipate(account, offerid).pipeTo(sender)

    case OfferServiceActor.getRecommended(account) =>
      offerDAO.getRecommended(account).pipeTo(sender)

    case OfferServiceActor.getPending(account) =>
      offerDAO.getPending(account).pipeTo(sender)

    case OfferServiceActor.getCompleted(account) =>
      offerDAO.getCompleted(account).pipeTo(sender)
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

  def list(account: Account, lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    implicit val timeout = OfferService.ListTimeout
    val futureResult = actor ? OfferServiceActor.ListOffers(account, lastID)

    futureResult.map { result =>
      result.asInstanceOf[Seq[Offer]]
    }
  }

  def get(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.GetOffer(account, offerid)

    futureResult.map { result =>
      result.asInstanceOf[Option[Offer]]
    }
  }

  def participate(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.Participate(account, offerid)

    futureResult.map { result =>
      result.asInstanceOf[Option[Boolean]]
    }
  }

  def unparticipate(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.UnParticipate(account, offerid)

    futureResult.map { result =>
      result.asInstanceOf[Option[Boolean]]
    }
  }

  def getRecommended(account: Account)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.getRecommended(account)

    futureResult.map { result =>
      result.asInstanceOf[Seq[Offer]]
    }
  }

  def getPending(account: Account)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.getPending(account)

    futureResult.map { result =>
      result.asInstanceOf[Seq[Offer]]
    }
  }

  def getCompleted(account: Account)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    implicit val timeout = OfferService.GetTimeout
    val futureResult = actor ? OfferServiceActor.getCompleted(account)

    futureResult.map { result =>
      result.asInstanceOf[Seq[Offer]]
    }
  }

}
