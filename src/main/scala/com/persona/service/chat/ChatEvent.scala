package com.persona.service.chat

import akka.actor.ActorRef
import com.persona.util.json.{DateTimeJsonProtocol, UuidJsonProtocol}
import org.joda.time.DateTime
import spray.json.DefaultJsonProtocol

sealed trait ChatEvent
case class Connect(user: String, timeStamp: DateTime, ref: ActorRef) extends ChatEvent
case class Disconnect(user: String, timeStamp: DateTime) extends ChatEvent
case class ChatMessage(user: String, msg: String, timeStamp: DateTime) extends ChatEvent
case class HistoryMessage(user: String, msg: ChatMessage)
case class AckMessage(user: String, timeStamp: DateTime) extends ChatEvent

trait ChatJsonProtocol extends DefaultJsonProtocol with UuidJsonProtocol with DateTimeJsonProtocol {

  implicit val dataItemJsonParser = jsonFormat3(ChatMessage)

}
