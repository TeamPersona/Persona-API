package com.persona.service.offer

import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContext, Future}
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

class PostgresOfferDataDAO(db: Database) extends TableQuery(new OfferDataTable(_)) with OfferDAO  {
  val offers = TableQuery[OfferDataTable]

  def list(lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    val offerBasicInfo = listBasic(lastID)

    offerBasicInfo.flatMap { optionOfferInfo =>
      val seqFuture = optionOfferInfo.map { offerInfo =>
        createOffer(offerInfo)
      }
      Future.sequence(seqFuture)
    }
  }

  def get(getId: Int)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    val offerBasicInfo = getBasic(getId)
    offerBasicInfo.map { offerOptionInfo =>
      offerOptionInfo.map { offerInfo =>
        createOffer(offerInfo)
      }
    }.flatMap { offer =>
      offer.map { f => f.map(Option(_))
      }.getOrElse(Future.successful(None))
    }
  }

  def participate(offerid: Int, userid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    val action = sql"SELECT public.participate(#$offerid,#$userid);".as[(Boolean)].headOption
    db.run(action)

  }

  def unparticipate(offerid: Int, userid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
    val action = sql"SELECT public.unparticipate(#$offerid,#$userid);".as[(Boolean)].headOption
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

  private def getFiltersSQL(getId: Int)(implicit ec: ExecutionContext): Future[Vector[(String, String)]] = {
    val offerid = getId.toString
    val action = sql"""SELECT
                       picategory.category,
                       pifields.pifield
                       FROM offercriteria
                       INNER JOIN pifields
                         ON offercriteria.pifieldid = pifields.pifieldid
                       INNER JOIN picategory
                         ON picategory.picategoryid = pifields.picategoryid
                       WHERE offercriteria.offercriteriasubindex = 1
                       AND offercriteria.offerid = #$offerid""".as[(String, String)]
    db.run(action)
  }


  private def getRequiredInfoSQL(getId: Int)(implicit ec: ExecutionContext): Future[Vector[(String, String)]] = {
    val offerid = getId.toString
    val action = sql"""SELECT
                       picategory.category,
                       pifields.pifield
                       FROM offerinforequired
                       INNER JOIN pifields
                       	ON offerinforequired.pifieldid = pifields.pifieldid
                       INNER JOIN picategory
                       	ON picategory.picategoryid = pifields.picategoryid
                       WHERE offerinforequired.offerid = #$offerid""".as[(String, String)]
    db.run(action)
  }




  //TODO: need to return the categories that are not basic info, and check them in cassandra
  private def getTypesSQL(getId: Int)(implicit ec: ExecutionContext): Future[Vector[(String)]] = {
    val offerid = getId.toString
    val action = sql"""SELECT offertypes.offertype
                        FROM offercategories
                        INNER JOIN offertypes
                       	ON offercategories.offertypeid = offertypes.offertypeid
                       WHERE offercategories.offerid =  #$offerid
                       ORDER BY offercategories.typeindex""".as[(String)]
    db.run(action)
  }

    private def getParticipatingSQL(getId: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]] = {
      val offerid = getId.toString
      val userID = 5 //TODO: get userID
      val action = sql"SELECT CASE WHEN part >= 1 THEN TRUE ELSE FALSE END FROM (SELECT COUNT(*) as part FROM offerparticipation WHERE offerid = #$offerid AND userid = #$userID) as participating;".as[(Boolean)].headOption
      db.run(action)
    }



  // TODO: call taylor's to get this
  private def getEligibleBasic (getId: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    Future{true}
//    getEligibleBasicSQL(getId) match {
//      case Success(eligible) => eligible.getOrElse(false)
//      case Failure(f) => false
//    }
  }

  def createOffer(offerBasicInfo: OfferBasicInfo)(implicit ec: ExecutionContext) : Future[Offer] =  {
    val offerTypes = getTypesSQL(offerBasicInfo.offerID)
    val offerFilters = getFiltersSQL(offerBasicInfo.offerID)
    val offerRequiredInfo = getRequiredInfoSQL(offerBasicInfo.offerID)
    val offerParticipating = getParticipatingSQL(offerBasicInfo.offerID)
    val offerEligible = getEligibleBasic(offerBasicInfo.offerID)

    for {
      ot <- offerTypes
      of <- offerFilters
      ori <- offerRequiredInfo
      oe <- offerEligible
      op <- offerParticipating
    } yield generateOffer(offerBasicInfo, ot, of, ori, oe, op.getOrElse(false))

  }

  def generateOffer (offerBasicInfo: OfferBasicInfo,
                     offerTypes: Vector[(String)],
                     offerFilters: Vector[(String, String)],
                     offerRequiredInfo: Vector[(String, String)],
                     offerEligible: Boolean,
                     offerParticipating: Boolean): Offer = {

    val ot = offerTypes.toList

    var filterMap: Map[String, List[Map[String, String]]] = Map[String, List[Map[String, String]]]()
    offerFilters.groupBy(filter => filter._1).foreach { case (k, v) => filterMap += k -> createOfferFilter(v) }

    var requiredMap: Map[String, List[Map[String, String]]] = Map[String, List[Map[String, String]]]()
    offerRequiredInfo.groupBy(filter => filter._1).foreach { case (k, v) => requiredMap += k -> createOfferFilter(v) }

    val oe = offerEligible
    val op = offerParticipating

    new Offer (offerBasicInfo, ot, filterMap, requiredMap, oe, op)

  }

  private def createOfferFilter(filters: Vector[(String, String)]) : List[Map[String, String]] = {
    var filterList = ListBuffer[Map[String, String]]()
    filters.foreach { filter =>
      filterList += Map("informationType" -> filter._2,  "informationMissing" -> "true") //TODO: get from taylor
    }
    filterList.toList
  }

}