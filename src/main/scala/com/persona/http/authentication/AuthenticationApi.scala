package com.persona.http.authentication

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.nimbusds.jwt.SignedJWT
import com.persona.service.authentication.facebook.FacebookAuthService
import com.persona.service.authentication.google.GoogleAuthService
import com.persona.service.authentication.{BasicAuth, BasicAuthJsonProtocol, PersonaAuthService}

import scala.concurrent.ExecutionContext
import scala.util.{Success, Try}

class AuthenticationApi
  (
    personaAuthService: PersonaAuthService,
    facebookAuthService: FacebookAuthService,
    googleAuthService: GoogleAuthService
  )
  (
    implicit executionContext: ExecutionContext
  )
  extends SprayJsonSupport
    with BasicAuthJsonProtocol {

  val route = {
    pathPrefix("authenticate") {
      pathEndOrSingleSlash {
        post {
          formFields("id", "password").as(BasicAuth) { basicAuth =>
            complete(personaAuthService.authenticate(basicAuth))
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
              Try(SignedJWT.parse(jwt)) match {
                case Success(idToken) =>
                  onComplete(googleAuthService.authenticate(idToken)) {
                    case Success(authenticationResult) => complete(authenticationResult.toString)
                    case _ => complete(StatusCodes.InternalServerError)
                  }

                case _ => complete(StatusCodes.BadRequest)
              }
            }
          }
        }
      }
    }
  }

}
