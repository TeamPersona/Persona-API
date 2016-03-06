// did not want to change the other, might be a better way to combine the two since they're almost the same
package com.persona.util.json

import org.joda.time.DateTime
import spray.json.{DeserializationException, JsNumber, JsValue, RootJsonFormat}

trait DateTimeSecondsJsonProtocol {

  implicit object DateTimeSecondsJsonFormat extends RootJsonFormat[DateTime] {

    def write(dateTime: DateTime): JsValue = JsNumber(dateTime.getMillis / 1000)

    def read(dateTime: JsValue): DateTime = {
      dateTime match {
        case JsNumber(seconds) => new DateTime(seconds.longValue * 1000)
        case _ => throw new DeserializationException("Invalid date time")
      }
    }

  }

}
