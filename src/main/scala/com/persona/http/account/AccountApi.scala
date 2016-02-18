package com.persona.http.account

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.http.JsonPersonaError
import com.persona.service.account.{AccountValidator, AccountDescriptor, AccountService}

import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AccountApi
  (
    accountService: AccountService,
    accountValidator: AccountValidator
  )
  (
    implicit ec: ExecutionContext
  )
  extends SprayJsonSupport with JsonPersonaError {

  val route = {
    pathPrefix("account") {
      pathEndOrSingleSlash {
        post {
          formFields('given_name.as[String],
                     'family_name.as[String],
                     'email.as[String],
                     'phone_number.as[String]).as(AccountDescriptor) { accountDescriptor =>
            accountValidator.validate(accountDescriptor).fold({ errors =>
              complete(StatusCodes.BadRequest, errorJson(errors))
            }, { _ =>
              onComplete(accountService.create(accountDescriptor)) {
                case Success(validationResult) =>
                  if(validationResult.isSuccess) {
                    complete("""{ "code": "abcdefghikl" }""".parseJson)
                  } else {
                    complete(StatusCodes.Conflict)
                  }

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)
              }
            })
          }
        }
      } ~
      path("google") {
        pathEndOrSingleSlash {
          post {
            complete("""{ "code": "abcdefghikl" }""".parseJson)
          }
        }
      }
    }
  }

}
