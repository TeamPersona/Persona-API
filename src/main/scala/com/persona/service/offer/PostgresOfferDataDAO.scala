package com.persona.service.offer

import org.joda.time.DateTime
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

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
  def offerCategory = column[String]("offercategory")
  def offerType = column[String]("offertype")
  def offerStatus = column[String]("offerstatus")
  def rewardTier = column[Option[Int]]("mintierid")
  def maxParticipants = column[Int]("maxparticipants")
  def startTime = column[DateTime]("startingtime")
  def endTime = column[DateTime]("endingtime")
  def reward = column[Double]("reward")
  def currentParticipants = column[Int]("numparticipants")

  def *  = (offerID, partnerName, partnerID, partnerImageUrl, offerDetails, offerCategory, offerType, offerStatus, rewardTier, maxParticipants, startTime, endTime, reward, currentParticipants) <> (OfferBasicInfo.tupled, OfferBasicInfo.unapply)

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
        Offer (offerInfo, getRequiredCategories(offerInfo.offerID), getEligibleBasic(offerInfo.offerID)) //TODO: need eligibility from other data
      }
    }
  }

  def get(getId: Int)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    val offerBasicInfo = getBasic(getId)
    offerBasicInfo.map[Option[Offer]]{ optionOfferInfo =>
      optionOfferInfo.map { offerInfo =>
        Offer (offerInfo, getRequiredCategories(offerInfo.offerID), getEligibleBasic(offerInfo.offerID)) //TODO: need eligibility from other data
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

  private def getRequiredCategories (getId: Int)(implicit ec: ExecutionContext): List[OfferCriterionDescriptor] = {
    val returnDescriptors = ListBuffer[OfferCriterionDescriptor]()
    getRequiredCategoriesSQL(getId) match {
      case Success(criteria) => {
        criteria.foreach(criterion => returnDescriptors += OfferCriterionDescriptor(criterion._1, criterion._2))
        returnDescriptors.toList
      }
      case Failure(f) => List[OfferCriterionDescriptor]()
    }
  }

  //TODO: need to return the categories that are not basic info, and check them in cassandra
  private def getRequiredCategoriesSQL(getId: Int)(implicit ec: ExecutionContext): Try[Vector[(String, Boolean)]] = {
    val offerid = getId.toString
    val userID = 5 //TODO: get userID
    val action = sql"""SELECT pifields.pifield,
                        CASE WHEN pi.pifieldid is null THEN 'true'
                          ELSE 'false' END AS isMissing
                        FROM offercriteria
                        INNER JOIN pifields
                          ON offercriteria.pifieldid = pifields.pifieldid
                        LEFT JOIN (SELECT * FROM personalinformation WHERE personalinformation.userid = #$userID) AS pi
                          ON offercriteria.pifieldid = pi.pifieldid
                        WHERE offercriteria.offercriteriasubindex = 1
                        AND offercriteria.offerid = #$offerid""".as[(String, Boolean)]
    val criteria = db.run(action)
    Await.ready(criteria, Duration.Inf).value.get

  }

  private def getEligibleBasic (getId: Int)(implicit ec: ExecutionContext): Boolean = {
    getEligibleBasicSQL(getId) match {
      case Success(eligible) => eligible.getOrElse(false)
      case Failure(f) => false
    }
  }

  private def getEligibleBasicSQL(getId: Int)(implicit ec: ExecutionContext): Try[Option[Boolean]] = {
    val offerid = getId.toString
    val userID = 5 //TODO: get userID
    val action = sql"SELECT iseligible(#$offerid,#$userID); ".as[(Boolean)].headOption
    val criteria = db.run(action)
    Await.ready(criteria, Duration.Inf).value.get

  }






}