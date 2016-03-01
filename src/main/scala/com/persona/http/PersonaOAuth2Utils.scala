package com.persona.http

import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._

trait PersonaOAuth2Utils {

  def oauth2Token: Directive1[String] = {
    headerValue {
      case authorizationHeader: Authorization =>
        Some(authorizationHeader.credentials.token)

      case _ =>
        None
    }
  }

}
