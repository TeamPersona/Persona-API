package com.persona.service.chat

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.persona.service.chat.dao.ChatDAO
import scala.concurrent.duration._

class ChatService(dataAccess: ChatDAO)(implicit actorSystem: ActorSystem, materializer: Materializer) {

  implicit val chatDAO = dataAccess

  def chat(offerId: UUID, user: String, idleTimeout: FiniteDuration) = {
    ChatRoom.find(offerId) match {
      case Some(room) => handleWebsocketMessages(
        room.websocketFlow(user).keepAlive(1 minute, () => TextMessage.Strict("{\"type\":3}"))
      )
      case None => complete(StatusCodes.NotFound)
    }
  }

  def createRoom(offerId: UUID): Unit = {
    ChatRoom.createRoom(offerId)
  }

}