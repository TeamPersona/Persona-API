package com.persona.http.account

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.persona.service.account.AccountService

import spray.json._

class AccountApi(accountService: AccountService) extends SprayJsonSupport {

  val route = {
    pathPrefix("account") {
      pathEndOrSingleSlash {
        post {
          complete("""{ "code": "abcdefghikl" }""".parseJson)
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
