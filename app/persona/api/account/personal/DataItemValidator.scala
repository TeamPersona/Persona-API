package persona.api.account.personal

import com.google.inject.ImplementedBy
import persona.util.PersonaError

import scalaz.ValidationNel

sealed trait DataItemValidationError extends PersonaError

sealed class InvalidCategoryError(
  category: String,
  subcategory: String) extends DataItemValidationError {

  def errorMessage = "Unknown category (" + category + ", " + subcategory + ")"

}

sealed class InvalidFieldError(
  schema: DataSchema,
  fieldName: String) extends DataItemValidationError {

  def errorMessage = fieldName + " is not a member of (" + schema.category + ", " + schema.subcategory + ")"

}

sealed class TypeMismatchError(
  field: FieldDescriptor,
  expectedType: String,
  receivedValue: String) extends DataItemValidationError {

  def errorMessage = "Expected " + expectedType + " for field " + field.name + ", but got \"" + receivedValue + "\""

}

sealed class MissingRequiredFieldError(field: FieldDescriptor) extends DataItemValidationError {

  def errorMessage = "Missing required field " + field.name

}

sealed class InvalidDataException(message: String) extends RuntimeException(message)

@ImplementedBy(classOf[DataItemValidatorImpl])
trait DataItemValidator {

  def validate(item: DataItem): ValidationNel[DataItemValidationError, DataItem]

  def ensureValid(item: DataItem) = {
    if(validate(item).isFailure) {
      throw new InvalidDataException("Data item is invalid")
    }
  }

}
