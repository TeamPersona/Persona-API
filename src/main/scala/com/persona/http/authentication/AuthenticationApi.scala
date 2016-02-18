package com.persona.http.authentication

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.persona.service.authentication.facebook.FacebookAuthService
import com.persona.service.authentication.{BasicAuth, BasicAuthJsonProtocol, PersonaAuthService}

import scala.concurrent.ExecutionContext

class AuthenticationApi
  (
    personaAuthService: PersonaAuthService,
    facebookAuthService: FacebookAuthService
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
              complete("TODO")
            }
          }
        }
      }
    }
  }

}
