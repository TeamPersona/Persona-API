package com.persona.service.chat.dao

import java.util.UUID

import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import com.github.tototoshi.slick.PostgresJodaSupport._
import com.persona.service.chat.ChatMessage

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

class MsgHistoryTable(tag: Tag) extends Table[MsgHistory](tag, "msg_history") {

  def msgId = column[Int]("msg_id", O.AutoInc, O.PrimaryKey)
  def offerId = column[UUID]("offerid")
  def userId = column[String]("userid")
  def sender = column[String]("sender")
  def message = column[String]("message")
  def timestamp = column[DateTime]("timestamp")

  def idx = index("idx_msghistory", (offerId, userId), unique = true)

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

  // def idx = index("idx_msg_ack", (offerId, userId), unique = true)

  override def * = (offerId, userId, msgId) <> (MsgAck.tupled, MsgAck.unapply)

}

class ChatDAO(db: Database) {

  private[this] val msgHistory = TableQuery[MsgHistoryTable]
  private[this] val msgAck = TableQuery[MessageAcksTable]

  def fetchMsgHistory(offerId: UUID, userid: String) = {
    val query = msgHistory.filter(
      row => row.offerId === offerId && row.userId === userid
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

}