package persona.api.account.personal

class DataSchema private(
  val category: String,
  val subcategory: String,
  fieldDescriptors: Map[String, FieldDescriptor]) {

  if(fieldDescriptors.isEmpty) {
    throw new InvalidSchemaException("Data schema " + (category, subcategory).toString + " has no fields")
  }

  private[this] val requiredFields = fieldDescriptors.filter(descriptor => descriptor._2.isRequired).keys

  def this(category: String, subcategory:String, fields: Seq[FieldDescriptor]) = {
    this(category, subcategory, fields.map(field => field.name -> field).toMap)
  }

  def validate(item: DataItem): Boolean = {
    hasAllRequiredFields(item) && meetsConstraints(item)
  }

  private[this] def meetsConstraints(item: DataItem): Boolean = {
    item.data.forall { field =>
      val fieldDescriptor = fieldDescriptors.get(field._1)

      fieldDescriptor.exists(descriptor => descriptor.validate(field._2))
    }
  }

  private[this] def hasAllRequiredFields(item: DataItem): Boolean = {
    requiredFields.forall(field => item.data.contains(field))
  }
}

object DataSchema {

  def apply(category: String, subcategory: String, fields: Seq[FieldDescriptor]): DataSchema = {
    new DataSchema(category, subcategory, fields)
  }

}
