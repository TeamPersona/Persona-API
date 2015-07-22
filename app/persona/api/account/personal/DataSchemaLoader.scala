package persona.api.account.personal

import com.google.inject.ImplementedBy

sealed class InvalidSchemaException(message: String) extends RuntimeException(message)

@ImplementedBy(classOf[JsonDataSchemaLoader])
trait DataSchemaLoader {

  def load: Seq[DataSchema]

}
