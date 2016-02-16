package com.persona.service.authentication

import spray.json.DefaultJsonProtocol

case class BasicAuth(id: String, password: String)

trait BasicAuthJsonProtocol extends DefaultJsonProtocol {

  implicit val basicAuthJsonParser = jsonFormat2(BasicAuth)

}
