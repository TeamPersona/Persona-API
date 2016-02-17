package com.persona.service.chat

import java.util.UUID

import akka.actor.Actor
import com.persona.service.chat.dao.MsgHistoryDAO

sealed trait DBEvent
case class FetchHistory(offerId: UUID, userid: String) extends DBEvent
case class PersistMsg(offerId: UUID, userid: String, msg: ChatMessage) extends DBEvent


class ChatStorageActor extends Actor {

  override def receive = {
    case evt: FetchHistory =>
      val history = MsgHistoryDAO.fetchMsgHistory(evt.offerId, evt.userid)
      history.foreach(row => sender() ! HistoryMessage(evt.userid, row.message))

    case evt: PersistMsg =>
      MsgHistoryDAO.storeMsg(evt.offerId, evt.userid, evt.msg)
  }

}
