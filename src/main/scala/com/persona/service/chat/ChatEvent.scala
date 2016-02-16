package com.persona.service.chat

import akka.actor.ActorRef

sealed trait ChatEvent
case class Connect(user: String, ref: ActorRef) extends ChatEvent
case class Disconnect(user: String) extends ChatEvent
case class ChatMessage(user: String, msg: String) extends ChatEvent
case class HistoryMessage(user: String, msg: ChatMessage)
case class AckMessage(user: String) extends ChatEvent
