package com.persona.service.chat

import java.util.UUID

import akka.actor.{Props, ActorRef, Actor}
import com.persona.service.chat.dao.ChatDAO
import org.joda.time.DateTime

class ChatRoomActor(offerId: UUID, chatDAO: ChatDAO) extends Actor {

  var participants = Map.empty[String, ActorRef]
  var supports = Map.empty[String, ActorRef]

  val dbWorker = context.actorOf(Props(classOf[ChatStorageActor], chatDAO))

  override def receive = {
    case Connect(user, timestamp, ref) =>
      userType(user) match {
        case UserType.Consumer =>
          participants += user -> ref
          supports.values.foreach(_ ! ChatMessage(user, "Joined", timestamp))
          dbWorker ! FetchHistory(offerId, user)

        case UserType.Partner =>
          supports += user -> ref
      }

    case Disconnect(user, timestamp) =>
      userType(user) match {
        case UserType.Consumer =>
          participants -= user
        case UserType.Partner =>
          supports -= user
      }

    case AckMessage(user, timestamp) =>
      dbWorker ! PersistMsg(offerId, user, ChatMessage(user, "Seen", timestamp))
      supports.values.foreach(_ ! ChatMessage(user, "Seen", timestamp));

    case msg: ChatMessage =>
      if(userType(msg.user) == UserType.Partner) {
          participants.values.foreach(_ ! msg)
      }
      supports.values.foreach(_ ! msg)
      dbWorker ! PersistMsg(offerId, msg.user, msg)

    case msg: HistoryMessage =>
      participants.get(msg.user).get ! msg.msg

  }

  /*
   * TODO: Hook into account services when done
   */
  def userType(user: String) = {
    user match {
      case "support" => UserType.Partner
      case _ => UserType.Consumer
    }
  }

}
