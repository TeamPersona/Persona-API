package com.persona.http.chat

import akka.http.scaladsl.server.Directives._
import com.persona.service.chat.ChatService

class ChatApi(chatService: ChatService) {

  val route = get {
    pathPrefix("chat" / JavaUUID) { offerId =>
      pathEndOrSingleSlash {
        parameter('userid) { username =>
          chatService.chat(offerId, username)
        }
      } ~
      path("create") {
        chatService.createRoom(offerId)
        complete("Done")
      }
    }
  }

}
