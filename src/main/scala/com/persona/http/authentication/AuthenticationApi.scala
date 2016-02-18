package com.persona.http.authentication

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.service.authentication.AuthenticationService
import com.persona.service.authentication.facebook.FacebookAuthService

import scala.concurrent.ExecutionContext

import spray.json._

import scala.util.{Failure, Success}

class AuthenticationApi
  (
    authenticationService: AuthenticationService,
    facebookAuthService: FacebookAuthService
  )
  (
    implicit executionContext: ExecutionContext
  )
  extends SprayJsonSupport{

  val route = {
    pathPrefix("authenticate") {
      pathEndOrSingleSlash {
        post {
          formField('grant_type) {
            case "password" =>
              formFields('email, 'password) { (email, password) =>
                onComplete(authenticationService.authenticate(email, password)) {
                  case Success(authenticated) =>
                    if(authenticated) {
                      complete("""{ "code": "abcdefghikl" }""".parseJson)
                    } else {
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
      } ~
      path("facebook") {
        pathEndOrSingleSlash {
          post {
            complete(facebookAuthService.authenticate)
          }
        }
      } ~
      path("google") {
        pathEndOrSingleSlash {
          post {
            formField("id_token") { jwt =>
              complete("TODO")
            }
          }
        }
      }
    }
  }

}
