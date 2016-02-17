package com.persona.http.account

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._

import spray.json._

class AccountApi extends SprayJsonSupport {

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
