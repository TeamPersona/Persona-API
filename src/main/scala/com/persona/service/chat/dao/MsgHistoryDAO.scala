package com.persona.service.chat.dao

import java.util.UUID

import com.persona.service.chat.ChatMessage

import scala.collection.mutable
import scala.collection.mutable._

case class MsgHistoryRow(offerId: UUID, userid: String, message: ChatMessage)

object MsgHistoryDAO {

  var storage: MutableList[MsgHistoryRow] = mutable.MutableList.empty

  def fetchMsgHistory(offerId: UUID, userid: String) = {
    storage.filter(
      // TODO: Store user type then use that
      row => row.offerId == offerId && (row.userid == userid || row.userid == "support")
    )
  }

  def storeMsg(offerId: UUID, userid: String, message: ChatMessage) = {
    storage += MsgHistoryRow(offerId, userid, message)
  }

}
