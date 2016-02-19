package com.persona.http.authorization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.persona.service.account.thirdparty.ThirdPartyAccountDAO
import com.persona.service.authorization.AuthorizationService
import spray.json._

class AuthorizationApi(
  thirdPartyAccountDAO: ThirdPartyAccountDAO,
  authorizationService: AuthorizationService)
  extends SprayJsonSupport {

  val route = {
    pathPrefix("authorize") {
      pathEndOrSingleSlash {
        post {
          complete(
            """
            {
              "access_token" : "abcdefghijkl",
              "refresh_token" : "abcdefghijkl",
              "expires_in" : 3600,
              "token_type" : "Bearer",
              "id_token" : "abcdefghijkl"
            }""".parseJson)
        }
      }
    }
  }

}
