package persona.util

sealed trait ParseError extends PersonaError

sealed class BadFormatError(value: String) extends ParseError {

  def errorMessage = "Value is poorly formatted: " + value

}

sealed class ValidationError(validationMessage: String) extends ParseError {

  def errorMessage = validationMessage

}
