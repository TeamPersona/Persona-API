package com.persona.service.chat

import akka.actor.ActorRef
import com.persona.util.json.{DateTimeJsonProtocol, UuidJsonProtocol}
import org.joda.time.DateTime
import spray.json.DefaultJsonProtocol

object MessageType extends Enumeration {

  type MessageType = Value
  val Chat = 1
  val Ack = 2
  val Pong = 3

}

sealed trait ChatEvent
case class Connect(user: String, timeStamp: DateTime, ref: ActorRef) extends ChatEvent
case class Disconnect(user: String, timeStamp: DateTime) extends ChatEvent
case class ChatMessage(user: String, msg: String, timeStamp: DateTime) extends ChatEvent
case class HistoryMessage(user: String, msg: ChatMessage)
case class PongMessage(user: String) extends ChatEvent
case class AckMessage(user: String) extends ChatEvent

trait ChatJsonProtocol extends DefaultJsonProtocol with UuidJsonProtocol with DateTimeJsonProtocol {

  implicit val dataItemJsonParser = jsonFormat3(ChatMessage)

}
