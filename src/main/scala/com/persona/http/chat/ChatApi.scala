package com.persona.http.chat

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.http.PersonaOAuth2Utils
import com.persona.service.authorization.AuthorizationService
import com.persona.service.chat.ChatService
import com.persona.service.chat.dao.{DemoMessage, DemoJsonParser}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import spray.json._

class ChatApi(chatService: ChatService, authorizationService: AuthorizationService, idleTimeout: Int)(implicit ec: ExecutionContext)
  extends PersonaOAuth2Utils with DemoJsonParser {

  val route = get {
    pathPrefix("chat" / IntNumber) { offerId =>
      pathEndOrSingleSlash {
        onComplete(chatService.chatDAO.demoGetMessage(offerId)) {
          case Success(Some(msg)) =>
            complete(DemoMessage(None, msg.msg, msg.timestamp).toJson.compactPrint)

          case Success(None) =>
            complete(StatusCodes.NotFound)

          case Failure(e) =>
            complete(StatusCodes.InternalServerError)

        }
      }
//      path("create") {
//        chatService.createRoom(offerId)
//        complete(StatusCodes.OK)
//      }
    }
  }

}
