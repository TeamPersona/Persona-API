package persona.api.account.personal

import persona.util.converter.Converters._
import persona.util.validator.Validator

/**
 * Might expand this class in the future to include constraints.
 * For the moment it may seem a little pointless, but that's ok
 */
class FieldDescriptor private(
  val name: String,
  val isRequired: Boolean,
  typeValidator: Validator[String]) {

  def validate(data: String): Boolean = typeValidator.validate(data)

}

object FieldDescriptor {

  def apply(name: String, isRequired: Boolean, fieldType: String): FieldDescriptor = {
    val validator = getValidatorForType(fieldType)

    new FieldDescriptor(name, isRequired, validator)
  }

  private[this] def getValidatorForType(fieldType: String): Validator[String] = {
    val converter = fieldType.toLowerCase match {
      case "int" => new IntConverter
      case "long" => new LongConverter
      case "string" => new StringConverter
      case _ => throw new UnknownTypeException("Unknown type: " + fieldType)
    }

    converter.toValidator
  }
}