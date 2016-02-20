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
                       msgType: Int,
                       sender: String,
                       message: String,
                       timestamp: DateTime
                     )

class MsgHistoryTable(tag: Tag) extends Table[MsgHistory](tag, "msg_history") {

  def msgId = column[Int]("msg_id", O.AutoInc)
  def offerId = column[UUID]("offerid")
  def userId = column[String]("userid")
  def msgType = column[Int]("type")
  def sender = column[String]("sender")
  def message = column[String]("message")
  def timestamp = column[DateTime]("timestamp")

  def idx = index("idx_msghistory", (offerId, userId))

  override def * = (msgId, offerId, userId, msgType, sender, message, timestamp) <> (MsgHistory.tupled, MsgHistory.unapply)

}

class ChatDAO(db: Database) {

  private[this] val msgHistory = TableQuery[MsgHistoryTable]

  def fetchMsgHistory(offerId: UUID, userid: String) = {
    val query = msgHistory.filter(
      row => row.offerId === offerId && row.userId === userid
    ).map(row => (row.userId, row.sender, row.message, row.timestamp)).result
    db.run(query)
  }

  def storeMsg(offerId: UUID, userid: String, message: ChatMessage) = {
    val query = msgHistory += MsgHistory(0, offerId, userid, 1, message.user, message.msg, message.timeStamp)
    db.run(query)
  }

}