package com.persona.http

import com.persona.util.PersonaError
import spray.json.{JsArray, JsString, JsValue}

import scalaz.NonEmptyList

trait JsonPersonaError {

  def errorJson[T <: PersonaError](errors: NonEmptyList[T]): JsValue = {
    val errorMessages = errors.list.map { error =>
      JsString(error.errorMessage)
    }

    JsArray(errorMessages.toVector)
  }

}
