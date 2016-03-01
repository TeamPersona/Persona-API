package com.persona.service.bank

import com.persona.util.PersonaError

import scalaz.Scalaz._
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

  def errorMessage = "Expected " + expectedType + " for field " + field.name + ", but got " + receivedValue

}

sealed class MissingRequiredFieldError(field: FieldDescriptor) extends DataItemValidationError {

  def errorMessage = "Missing required field " + field.name

}

sealed class InvalidDataException(message: String) extends RuntimeException(message)

class DataItemValidator(dataSchemaManager: DataSchemaManager) {

  def validate(item: DataItem): ValidationNel[DataItemValidationError, DataItem] = {
    dataSchemaManager.schema(item.category, item.subcategory).map { schema =>
      schema.validate(item)
    } getOrElse {
      new InvalidCategoryError(item.category, item.subcategory).failureNel
    }
  }

}
