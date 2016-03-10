package com.persona.service.offer

import com.persona.service.account.Account
import com.persona.service.bank.CassandraBankDAO
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

class OfferDataTable(tag: Tag) extends Table[OfferBasicInfo] (tag,"view_offerdata"){

  def offerID = column[Int]("offerid")
  def partnerName = column[String]("partnername")
  def partnerID = column[Int]("partnerid")
  def partnerImageUrl = column[String]("partnerimageurl")
  def offerDetails = column[String]("offerdetails")
  def offerStatus = column[Int]("offerstatusid")
  def offerMinRank = column[Option[Int]]("mintierid")
  def offerMaxParticipants = column[Int]("maxparticipants")
  def offerStartDate = column[DateTime]("startingtime")
  def offerExpirationDate = column[DateTime]("endingtime")
  def offerReward = column[Double]("reward")
  def offerCurrentParticipants = column[Int]("numparticipants")
  def *  = (offerID, partnerName, partnerID, partnerImageUrl, offerDetails, offerStatus, offerMinRank, offerMaxParticipants, offerStartDate, offerExpirationDate, offerReward, offerCurrentParticipants) <> (OfferBasicInfo.tupled, OfferBasicInfo.unapply)

}

class PostgresOfferDataDAO(db: Database, cassandraBankDAO: CassandraBankDAO) extends TableQuery(new OfferDataTable(_)) with OfferDAO  {
  val offers = TableQuery[OfferDataTable]

  def list(account: Account, lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    val offerBasicInfo = listBasic(lastID)

    offerBasicInfo.flatMap { optionOfferInfo =>
      val seqFuture = optionOfferInfo.map { offerInfo =>
        createOffer(account, offerInfo)
      }
      Future.sequence(seqFuture)
    }
  }

  def get(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    val offerBasicInfo = getBasic(offerid)
    offerBasicInfo.map { offerOptionInfo =>
      offerOptionInfo.map { offerInfo =>
        createOffer(account, offerInfo)
      }
    }.flatMap { offer =>
      offer.map { f => f.map(Option(_))
      }.getOrElse(Future.successful(None))
    }
  }

  def participate(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    val offerBasicInfo = getBasic(offerid)

    val tryparticipate = for {
      obi <- offerBasicInfo
      of <- getFilters(obi.get.offerID)
      ori <- getRequiredInfo(obi.get.offerID)
      omp <- getHasPoints(account,obi.get.offerID)
      ofm <- cassandraBankDAO.has(account, of.toList)
      orim <- cassandraBankDAO.has(account, ori.toList)
    } yield {
      val eligible = isEligible(createIsMissing(of.toList, ofm).map(missing => !missing._2._2), createIsMissing(ori.toList, orim).map(missing => !missing._2._2), omp.getOrElse(false))
      if (eligible) {
        val action = sql"SELECT public.participate(#$offerid,#${account.id});".as[(Boolean)].headOption
        db.run(action)
      } else {
        Future{Option{false}}
      }
    }
    tryparticipate.flatMap(identity)
  }

  def unparticipate(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    val action = sql"SELECT public.unparticipate(#$offerid,#${account.id});".as[(Boolean)].headOption
    db.run(action)

  }

  def listBasic(lastID: Int)(implicit ec: ExecutionContext): Future[Seq[OfferBasicInfo]] = {
    val action = offers.filter(_.offerID > lastID).take(25).result
    db.run(action)
  }

  private def getBasic(getId: Int)(implicit ec: ExecutionContext): Future[Option[OfferBasicInfo]] = {
    val action = offers.filter(_.offerID === getId).result.headOption
    db.run(action)
  }

  private def getFilters(getId: Int)(implicit ec: ExecutionContext): Future[Vector[(String, String)]] = {
    val offerid = getId.toString
    val action = sql"SELECT * from public.getfilters(#$offerid);".as[(String, String)]
    db.run(action)
  }


  private def getRequiredInfo(getId: Int)(implicit ec: ExecutionContext): Future[Vector[(String, String)]] = {
    val offerid = getId.toString
    val action = sql"SELECT * from public.getrequiredinfo(#$offerid);".as[(String, String)]
    db.run(action)
  }


  private def getTypes(offerid: Int)(implicit ec: ExecutionContext): Future[Vector[(String)]] = {
    val action = sql"SELECT public.gettypes(#$offerid);".as[(String)]
    db.run(action)
  }

  private def getParticipating(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    val userID = account.id
    val action = sql"SELECT public.getparticipating(#$offerid,#$userID);".as[(Boolean)].headOption
    db.run(action)
  }

  private def getHasPoints(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    val userID = account.id
    val action = sql"SELECT public.haspoints(#$offerid,#$userID);".as[(Boolean)].headOption
    db.run(action)
  }


  def createOffer(account: Account, offerBasicInfo: OfferBasicInfo)(implicit ec: ExecutionContext) : Future[Offer] =  {
    val offerTypes = getTypes(offerBasicInfo.offerID)
    val offerFilters = getFilters(offerBasicInfo.offerID)
    val offerRequiredInfo = getRequiredInfo(offerBasicInfo.offerID)
    val offerParticipating = getParticipating(account, offerBasicInfo.offerID)
    val hasPoints = getHasPoints(account, offerBasicInfo.offerID)

    for {
      ot <- offerTypes
      of <- offerFilters
      ori <- offerRequiredInfo
      op <- offerParticipating
      omp <- hasPoints

      ofm <- cassandraBankDAO.has(account, of.toList)
      orim <- cassandraBankDAO.has(account, ori.toList)
    } yield generateOffer(offerBasicInfo, ot, createIsMissing(of.toList, ofm), createIsMissing(ori.toList, orim), isEligible(ofm, orim, omp.getOrElse(false)), op.getOrElse(false), account)

  }

  def generateOffer (offerBasicInfo: OfferBasicInfo,
                     offerTypes: Vector[(String)],
                     offerFilters: List[(String, (String, Boolean))],
                     offerRequiredInfo: List[(String, (String, Boolean))],
                     offerEligible: Boolean,
                     offerParticipating: Boolean,
                     account: Account): Offer = {

    val ot = offerTypes.toList

    var filterMap: Map[String, List[Map[String, String]]] = Map[String, List[Map[String, String]]]()
    offerFilters.groupBy(filter => filter._1).foreach { case (k, v) => filterMap += k -> createOfferFilter(v) }

    var requiredMap: Map[String, List[Map[String, String]]] = Map[String, List[Map[String, String]]]()
    offerRequiredInfo.groupBy(filter => filter._1).foreach { case (k, v) => requiredMap += k -> createOfferFilter(v) }

    val oe = offerEligible
    val op = offerParticipating

    new Offer (offerBasicInfo, ot, filterMap, requiredMap, oe, op)

  }

  private def createOfferFilter(filters: List[(String, (String, Boolean))]) : List[Map[String, String]] = {
    var filterList = ListBuffer[Map[String, String]]()
    filters.foreach { filter =>
      filterList += Map("informationType" -> filter._2._1,  "informationMissing" -> filter._2._2.toString)
    }
    filterList.toList
  }


  def createIsMissing(filters: List[(String, String)], has: List[Boolean]) : List[(String, (String, Boolean))] = {
    // has() is if the information is there, but we need if it is missing
    val filtersMissing = filters.zip(has.map(hasinfo => !hasinfo))
    filtersMissing.map { filter =>
      (filter._1._1, (filter._1._2, filter._2))
    }
  }

  private def isEligible (filters: List[Boolean], required: List[Boolean], hasPoints: Boolean)(implicit ec: ExecutionContext): Boolean = {
    !filters.contains(false) && !required.contains(false) && hasPoints
  }

}