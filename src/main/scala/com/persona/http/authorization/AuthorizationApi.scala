package com.persona.http.authorization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import com.persona.service.authorization.{AuthorizationResultJsonProtocol, AuthorizationService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AuthorizationApi
  (
    authorizationService: AuthorizationService
  )
  (
    implicit ec: ExecutionContext
  )
  extends SprayJsonSupport
    with AuthorizationResultJsonProtocol {

  val route = {
    pathPrefix("authorize") {
      pathEndOrSingleSlash {
        post {
          formField('grant_type) {
            case "authorization_code" =>
              formFields('code, 'client_id) { (code, clientId) =>
                onComplete(authorizationService.authorizationCodeGrant(code, clientId)) {
                  case Success(result) =>
                    result match {
                      case Some(authorizationResult) =>
                        complete(authorizationResult)

                      case None =>
                        complete(StatusCodes.BadRequest)
                    }

                  case Failure(e) =>
                    complete(StatusCodes.InternalServerError)
                }
              }

            case "refresh_token" =>
              formFields('refresh_token, 'client_id) { (refreshToken, clientId) =>
                onComplete(authorizationService.refreshTokenGrant(refreshToken, clientId)) {
                  case Success(result) =>
                    result match {
                      case Some(authorizationResult) =>
                        complete(authorizationResult)

                      case None =>
                        complete(StatusCodes.BadRequest)
                    }

                  case Failure(e) =>
                    complete(StatusCodes.InternalServerError)
                }
              }

            case _ =>
              complete(StatusCodes.BadRequest)
          }
        }
      }
    }
  }

}
