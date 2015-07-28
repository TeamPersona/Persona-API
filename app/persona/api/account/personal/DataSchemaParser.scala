package persona.api.account.personal

import com.google.inject.ImplementedBy

import scalaz.ValidationNel

sealed trait DataSchemaParseError {

  def errorMessage: String

}

sealed class BadFormatError(value: String) extends DataSchemaParseError {

  def errorMessage = "Value is poorly formatted: " + value

}

sealed class ValidationError(validationMessage: String) extends DataSchemaParseError {

  def errorMessage = validationMessage

}

@ImplementedBy(classOf[JsonDataSchemaParser])
trait DataSchemaParser {

  def parse(value: String): ValidationNel[DataSchemaParseError, DataSchema]

}
