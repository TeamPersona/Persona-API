package com.persona.service.chat.dao

import java.util.UUID

import com.persona.util.json.{DateTimeJsonProtocol, UuidJsonProtocol}
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import com.github.tototoshi.slick.PostgresJodaSupport._
import com.persona.service.chat.ChatMessage
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

case class MsgHistory(
                       msgId: Int,
                       offerId: UUID,
                       userId: String,
                       sender: String,
                       message: String,
                       timestamp: DateTime
                     )

case class MsgAck(
                     offerId: UUID,
                     userId: String,
                     timestamp: DateTime
                     )

case class DemoMessage(
                      offerId: Option[Int],
                      msg: String,
                      timestamp: DateTime
                      )

trait DemoJsonParser extends DefaultJsonProtocol with UuidJsonProtocol with DateTimeJsonProtocol {

  implicit val demoJsonParser = jsonFormat3(DemoMessage)

}


class MsgHistoryTable(tag: Tag) extends Table[MsgHistory](tag, "msg_history") {

  def msgId = column[Int]("msg_id", O.AutoInc, O.PrimaryKey)
  def offerId = column[UUID]("offerid")
  def userId = column[String]("userid")
  def sender = column[String]("sender")
  def message = column[String]("message")
  def timestamp = column[DateTime]("timestamp")

  def idx = index("idx_msghistory", (offerId, userId))

  override def * = (msgId, offerId, userId, sender, message, timestamp) <> (MsgHistory.tupled, MsgHistory.unapply)

}

class MessageAcksTable(tag: Tag) extends Table[MsgAck](tag, "msg_ack") {

  /*
   * Only way to get insertOrUpdate() functioning
   * Defect URL: https://github.com/slick/slick/issues/966
   */
  def offerId = column[UUID]("offerid", O.PrimaryKey)
  def userId = column[String]("userid", O.PrimaryKey)
  def msgId = column[DateTime]("timestamp")

  // def idx = index("idx_msg_ack", (offerId, userId))

  override def * = (offerId, userId, msgId) <> (MsgAck.tupled, MsgAck.unapply)

}

class ChatOfferTable(tag: Tag) extends Table[UUID](tag, "chat_offers") {

  def offerId = column[UUID]("offerid", O.PrimaryKey)

  override def * = offerId

}

class DemoMessageTable(tag: Tag) extends Table[DemoMessage](tag, "demo_message") {

  def offerId = column[Option[Int]]("offerid", O.PrimaryKey)
  def message = column[String]("message")
  def timestamp = column[DateTime]("timestamp")

  override def * = (offerId, message, timestamp) <> (DemoMessage.tupled, DemoMessage.unapply)

}

class ChatDAO(db: Database) {

  private[this] val msgHistory = TableQuery[MsgHistoryTable]
  private[this] val msgAck = TableQuery[MessageAcksTable]
  private[this] val chatOffer = TableQuery[ChatOfferTable]
  private[this] val demoQuery = TableQuery[DemoMessageTable]

  def fetchMsgHistory(offerId: UUID, userid: String) = {
    val validUsers = List(userid, "support")
    val query = msgHistory.filter(
      // TODO: Hardcoded to support
      row => row.offerId === offerId && row.userId.inSet(validUsers)
    ).map(row => (row.userId, row.sender, row.message, row.timestamp)).result
    db.run(query)
  }

  def storeMsg(offerId: UUID, userid: String, message: ChatMessage) = {
    val query = msgHistory += MsgHistory(0, offerId, userid, message.user, message.msg, message.timeStamp)
    db.run(query)
  }

  def ackMsg(offerId: UUID, userid: String, timestamp: DateTime) = {
    val query = msgAck.insertOrUpdate(MsgAck(offerId, userid, timestamp))
    db.run(query)
  }

  def getAllOfferId() = {
    val query = chatOffer.result
    db.run(query)
  }

  def createRoom(offerId: UUID) = {
    val query = chatOffer += offerId
    db.run(query)
  }

  def demoGetMessage(offerId: Int) = {
    val query = demoQuery.filter(_.offerId === offerId).result.headOption
    db.run(query)
  }

  def getAllDemoMessage(): Future[Seq[DemoMessage]] = {
    val query = demoQuery.result
    db.run(query)
  }

}