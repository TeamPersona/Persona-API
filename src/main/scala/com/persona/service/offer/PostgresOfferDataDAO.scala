package com.persona.service.offer

import org.joda.time.DateTime

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

import scala.util.{Failure, Success, Try}

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
//  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("persona")
//  val db = dbConfig.db
//  import dbConfig.driver.api._
  val offers = TableQuery[OfferDataTable]

  def list(lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]] = {
    val offerBasicInfo = listBasic(lastID)
    offerBasicInfo.map[Seq[Offer]]{ optionOfferInfo =>
      optionOfferInfo.map { offerInfo =>
        new Offer (offerInfo, getTypes(offerInfo.offerID), getFilters(offerInfo.offerID), getRequiredInfo(offerInfo.offerID), getEligibleBasic(offerInfo.offerID), getParticipating(offerInfo.offerID)) //TODO: need eligibility from other data
      }
    }
  }

  def get(getId: Int)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    val offerBasicInfo = getBasic(getId)
    offerBasicInfo.map[Option[Offer]]{ optionOfferInfo =>
      optionOfferInfo.map { offerInfo =>
       new Offer (offerInfo, getTypes(offerInfo.offerID), getFilters(offerInfo.offerID), getRequiredInfo(offerInfo.offerID), getEligibleBasic(offerInfo.offerID), getParticipating(offerInfo.offerID)) //TODO: need eligibility from other data
      }
    }
  }

  def listBasic(lastID: Int)(implicit ec: ExecutionContext): Future[Seq[OfferBasicInfo]] = {
    val action = offers.filter(_.offerID > lastID).take(25).result
    db.run(action)
  }

  private def getBasic(getId: Int)(implicit ec: ExecutionContext): Future[Option[OfferBasicInfo]] = {
    val action = offers.filter(_.offerID === getId).result.headOption
    db.run(action)
  }

  private def getFilters (getId: Int)(implicit ec: ExecutionContext): Map[String, List[Map[String, String]]] = {
    var filterMap: Map[String, List[Map[String, String]]] = Map[String, List[Map[String, String]]]()
    getFiltersSQL(getId) match {
      case Success(filters) => {
        filters.groupBy(filter => filter._1).foreach { case (k, v) =>
          filterMap += k -> createOfferFilter(v)
        }
        filterMap
      }
      case Failure(f) => Map[String, List[Map[String, String]]]()
    }
  }

  private def getFiltersSQL(getId: Int)(implicit ec: ExecutionContext): Try[Vector[(String, String)]] = {
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
    val criteria = db.run(action)
    Await.ready(criteria, Duration.Inf).value.get

  }

  private def getRequiredInfo (getId: Int)(implicit ec: ExecutionContext): Map[String, List[Map[String, String]]] = {
    var filterMap: Map[String, List[Map[String, String]]] = Map[String, List[Map[String, String]]]()
    getRequiredInfoSQL(getId) match {
      case Success(filters) => {
        filters.groupBy(filter => filter._1).foreach { case (k, v) =>
          filterMap += k -> createOfferFilter(v)
        }
        filterMap
      }
      case Failure(f) => Map[String, List[Map[String, String]]]()
    }
  }


  private def getRequiredInfoSQL(getId: Int)(implicit ec: ExecutionContext): Try[Vector[(String, String)]] = {
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
    val criteria = db.run(action)
    Await.ready(criteria, Duration.Inf).value.get

  }


  private def createOfferFilter(filters: Vector[(String, String)]) : List[Map[String, String]] = {
    var filterList = ListBuffer[Map[String, String]]()
    filters.foreach { filter =>
      filterList += Map("informationType" -> filter._2,  "informationMissing" -> "true") //TODO: get from taylor, also JSON thing can't do any, so using string for the bool for now
    }
    filterList.toList
  }


  private def getTypes (getId: Int)(implicit ec: ExecutionContext): List[String] = {
    geTypesSQL(getId) match {
      case Success(filters) => {
        filters.toList
      }
      case Failure(f) => List()
    }
  }


  //TODO: need to return the categories that are not basic info, and check them in cassandra
  private def geTypesSQL(getId: Int)(implicit ec: ExecutionContext): Try[Vector[(String)]] = {
    val offerid = getId.toString
    val action = sql"""SELECT offertypes.offertype
                        FROM offercategories
                        INNER JOIN offertypes
                       	ON offercategories.offertypeid = offertypes.offertypeid
                       WHERE offercategories.offerid =  #$offerid
                       ORDER BY offercategories.typeindex""".as[(String)]
    val criteria = db.run(action)
    Await.ready(criteria, Duration.Inf).value.get

  }

  private def getParticipating (getId: Int)(implicit ec: ExecutionContext): Boolean = {
    getParticipatingSQL(getId) match {
          case Success(participating) => participating.getOrElse(false)
          case Failure(f) => false
        }
  }



    private def getParticipatingSQL(getId: Int)(implicit ec: ExecutionContext): Try[Option[Boolean]] = {
      val offerid = getId.toString
      val userID = 5 //TODO: get userID
      val action = sql"SELECT CASE WHEN part >= 1 THEN TRUE ELSE FALSE END FROM (SELECT COUNT(*) as part FROM offerparticipation WHERE offerid = #$offerid AND userid = #$userID) as participating;".as[(Boolean)].headOption
      val criteria = db.run(action)
      Await.ready(criteria, Duration.Inf).value.get

    }



  // TODO: call taylor's to get this
  private def getEligibleBasic (getId: Int)(implicit ec: ExecutionContext): Boolean = {
    true
//    getEligibleBasicSQL(getId) match {
//      case Success(eligible) => eligible.getOrElse(false)
//      case Failure(f) => false
//    }
  }


//
//  private def getEligibleBasicSQL(getId: Int)(implicit ec: ExecutionContext): Try[Option[Boolean]] = {
//    val offerid = getId.toString
//    val userID = 5 //TODO: get userID
//    val action = sql"SELECT iseligible(#$offerid,#$userID); ".as[(Boolean)].headOption
//    val criteria = db.run(action)
//    Await.ready(criteria, Duration.Inf).value.get
//
//  }






}