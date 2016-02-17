package com.persona.util.json

import org.joda.time.DateTime
import spray.json.{DeserializationException, JsNumber, JsValue, RootJsonFormat}

trait DateTimeJsonProtocol {

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {

    def write(dateTime: DateTime): JsValue = JsNumber(dateTime.getMillis)

    def read(dateTime: JsValue): DateTime = {
      dateTime match {
        case JsNumber(millis) => new DateTime(millis.longValue)
        case _ => throw new DeserializationException("Invalid date time")
      }
    }

  }

}
