package com.persona.http.chat

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.http.PersonaOAuth2Utils
import com.persona.service.authorization.AuthorizationService
import com.persona.service.chat.ChatService
import com.persona.service.chat.dao.DemoJsonParser
import com.persona.service.offer.OfferService

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import spray.json._

class ChatApi(chatService: ChatService, offerService: OfferService, authorizationService: AuthorizationService, idleTimeout: Int)(implicit ec: ExecutionContext)
  extends PersonaOAuth2Utils with DemoJsonParser with SprayJsonSupport {

  val route = get {
    pathPrefix("chat") {
      pathEndOrSingleSlash {
        oauth2Token { token =>
          onComplete(authorizationService.validate(token)) {
            case Success(Some((account, _))) => {
              onComplete(chatService.chatDAO.getAllDemoMessage()) {
                case Success(msgs) =>
                  complete(StatusCodes.OK, msgs.toJson)

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)

              }
            }
            case Success(None) =>
              complete(StatusCodes.BadRequest)

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
}
