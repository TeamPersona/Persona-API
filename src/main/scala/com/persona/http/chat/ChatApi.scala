package com.persona.http.chat

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.service.chat.ChatService

import scala.concurrent.duration._

class ChatApi(chatService: ChatService, idleTimeout: Int) {

  val route = get {
    pathPrefix("chat" / JavaUUID) { offerId =>
      pathEndOrSingleSlash {
        parameter('userid) { username =>
          chatService.chat(offerId, username, idleTimeout seconds)
        }
      } ~
      path("create") {
        chatService.createRoom(offerId)
        complete(StatusCodes.OK)
      }
    }
  }

}
