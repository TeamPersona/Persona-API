package com.persona.service.bank

import persona.util.converter.Converters._
import persona.util.validator.Validator

import scalaz.Scalaz._
import scalaz.ValidationNel

/**
 * Might expand this class in the future to include constraints.
 * For the moment it may seem a little pointless, but that's ok
 */
class FieldDescriptor (
  val name: String,
  val isRequired: Boolean,
  val fieldType: String,
  typeValidator: Validator[String]) {

  require(name != null && !name.isEmpty)
  require(fieldType != null && !fieldType.isEmpty)
  require(typeValidator != null)
  
  def validate(data: String): ValidationNel[DataItemValidationError, String] = {
    validateType(data)
  }

  def validateType(data: String): ValidationNel[DataItemValidationError, String] = {
    if(typeValidator.validate(data)) {
      data.successNel
    }
    else {
      new TypeMismatchError(this, fieldType, data).failureNel
    }
  }

}

object FieldDescriptor {

  def apply(name: String, isRequired: Boolean, fieldType: String): FieldDescriptor = {
    val validator = getValidatorForType(fieldType)

    new FieldDescriptor(name, isRequired, fieldType, validator)
  }

  private[this] def getValidatorForType(fieldType: String): Validator[String] = {
    val converter = fieldType.toLowerCase match {
      case "int" => new IntConverter
      case "long" => new LongConverter
      case "string" => new StringConverter
      case _ => throw new IllegalArgumentException("Unknown type: " + fieldType)
    }

    converter.toValidator
  }

}
