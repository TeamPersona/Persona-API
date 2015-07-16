package persona.util.converter

import persona.util.validator.Validator

import scala.util.Try

trait Converter[-From, +To] {

  def convert(value: From): Try[To]

  def toValidator: Validator[From] = new Validator[From] {
    def validate(value: From): Boolean = convert(value).isSuccess
  }

}