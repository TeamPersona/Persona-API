package com.persona.http.chat

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.http.PersonaOAuth2Utils
import com.persona.service.authorization.AuthorizationService
import com.persona.service.chat.ChatService

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ChatApi(chatService: ChatService, authorizationService: AuthorizationService, idleTimeout: Int)(implicit ec: ExecutionContext)
  extends PersonaOAuth2Utils {

  val route = get {
    pathPrefix("chat" / JavaUUID) { offerId =>
      pathEndOrSingleSlash {
        parameter('userid) { username =>
//          oauth2Token { token =>
//            onComplete(authorizationService.validate(token)) {
//              case Success(Some((account, _))) =>
                chatService.chat(offerId, username, idleTimeout seconds)
//
//              case Success(None) =>
//                complete(StatusCodes.BadRequest)
//
//              case Failure(e) =>
//                complete(StatusCodes.InternalServerError)
//            }
//          }
//        }
      } ~
      path("create") {
        chatService.createRoom(offerId)
        complete(StatusCodes.OK)
      }
    }
  }

}
