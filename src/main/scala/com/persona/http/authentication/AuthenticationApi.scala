package com.persona.http.authentication

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.nimbusds.jwt.SignedJWT
import com.persona.service.authentication.AuthenticationService
import com.persona.service.authentication.google.{ValidTokenAndAccountResult, ValidTokenInvalidAccountResult, InvalidTokenAndAccountResult, GoogleAuthenticationService}
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.{Try, Failure, Success}

class AuthenticationApi
  (
    authenticationService: AuthenticationService,
    googleAuthenticationService: GoogleAuthenticationService
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
      path("google") {
        pathEndOrSingleSlash {
          post {
            formField("id_token") { jwt =>
              Try(SignedJWT.parse(jwt)) match {
                case Success(idToken) =>
                  onComplete(googleAuthenticationService.authenticate(idToken)) {
                    case Success(_: ValidTokenAndAccountResult) =>
                      complete("""{ "code": "abcdefghikl" }""".parseJson)

                    case Success(_: ValidTokenInvalidAccountResult) =>
                      complete(StatusCodes.NotFound)

                    case Success(_: InvalidTokenAndAccountResult) =>
                      complete(StatusCodes.BadRequest)

                    case Failure(e) =>
                      complete(StatusCodes.InternalServerError)
                  }

                case Failure(e) =>
                  complete(StatusCodes.BadRequest)
              }
            }
          }
        }
      }
    }
  }

}
