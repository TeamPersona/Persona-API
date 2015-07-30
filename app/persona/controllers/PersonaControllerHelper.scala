package persona.controllers

import persona.util.PersonaError
import play.api.libs.json.{JsValue, Json}

import scalaz.NonEmptyList

trait PersonaControllerHelper {

  def generateSuccessJson: JsValue = Json.obj("status" -> "success")

  def generateErrorJson(errors: NonEmptyList[PersonaError]): JsValue = {
    val errorMessages = errors.list.map(error => Json.obj("error" -> error.errorMessage))

    Json.obj("status" -> "failure", "errors" -> errorMessages)
  }

}
