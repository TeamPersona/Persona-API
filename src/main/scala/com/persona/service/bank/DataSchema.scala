package com.persona.service.bank

import scalaz.Scalaz._
import scalaz.Unapply._
import scalaz.ValidationNel

class DataSchema private(
  val category: String,
  val subcategory: String,
  val fieldDescriptors: Map[String, FieldDescriptor]) {

  require(category != null && !category.isEmpty)
  require(subcategory != null && !category.isEmpty)
  require(fieldDescriptors != null && fieldDescriptors.nonEmpty)

  private[this] val requiredFields = fieldDescriptors.filter(descriptor => descriptor._2.isRequired).values

  def validate(item: DataItem): ValidationNel[DataItemValidationError, DataItem] = {
    (hasAllRequiredFields(item) |@| meetsConstraints(item)) { (_, _) =>
      item
    }
  }

  private[this] def meetsConstraints(item: DataItem): ValidationNel[DataItemValidationError, _] = {
    val constraintValidation = item.data map { field =>
      val fieldName = field._1
      val fieldValue = field._2
      val maybeFieldDescriptor = fieldDescriptors.get(fieldName)

      maybeFieldDescriptor map { fieldDescriptor =>
        fieldDescriptor.validate(fieldValue)
      } getOrElse {
        new InvalidFieldError(this, fieldName).failureNel
      }
    }

    // List[ValidationNel[DataItemValidationError, _]] -> ValidationNel[DataItemValidationError, _]
    // IntelliJ is probably going to complain about this.  Don't worry, it's wrong
    constraintValidation.toList.sequenceU
  }

  private[this] def hasAllRequiredFields(item: DataItem): ValidationNel[DataItemValidationError, _] = {
    val requiredFieldsValidation = requiredFields map { requiredField =>
      if (item.data.contains(requiredField.name)) {
        item.successNel
      }
      else {
        new MissingRequiredFieldError(requiredField).failureNel
      }
    }

    // List[ValidationNel[DataItemValidationError, _]] -> ValidationNel[DataItemValidationError, _]
    // IntelliJ is probably going to complain about this.  Don't worry, it's wrong
    requiredFieldsValidation.toList.sequenceU
  }
}

object DataSchema {

  def apply(category: String, subcategory:String, fields: Seq[FieldDescriptor]) = {
    val fieldsAsMap = fields.map { field =>
      field.name -> field
    }.toMap

    new DataSchema(category, subcategory, fieldsAsMap)
  }

}
