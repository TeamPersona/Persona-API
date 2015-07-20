package persona.api.account.personal

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[FileDataSchemaLoader])
trait DataSchemaLoader {

  def load: Seq[DataSchema]

}
