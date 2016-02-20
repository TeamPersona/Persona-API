package com.persona.service.chat

import java.util.UUID

import akka.actor.{Status, Actor}
import com.persona.service.chat.dao.ChatDAO

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

sealed trait DBEvent
case class FetchHistory(offerId: UUID, userid: String) extends DBEvent
case class PersistMsg(offerId: UUID, userid: String, msg: ChatMessage) extends DBEvent

class ChatStorageActor(chatDAO: ChatDAO) extends Actor {

  override def receive = {
    case evt: FetchHistory =>
      val roomActor = sender
      chatDAO.fetchMsgHistory(evt.offerId, evt.userid)
        .onComplete {
          case Success(msgs) => {
            msgs.foreach(msg => roomActor ! HistoryMessage(msg._1, ChatMessage(msg._2, msg._3, msg._4)))
          }

          case Failure(e) => {
            roomActor ! Status.Failure(e)
          }
        }


    case evt: PersistMsg =>
      val roomActor = sender
      chatDAO.storeMsg(evt.offerId, evt.userid, evt.msg).onFailure {
        case e => roomActor ! Status.Failure(e)
      }
  }

}
