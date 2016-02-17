package com.persona.util.json

import java.util.UUID

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

trait UuidJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {

    def write(uuid: UUID): JsValue = JsString(uuid.toString)

    def read(uuid: JsValue): UUID = {
      uuid match {
        case JsString(value) => UUID.fromString(value)
        case _ => throw new DeserializationException("Invalid uuid")
      }
    }

  }

}
