package persona.api.account.personal

import com.google.inject.ImplementedBy

import scala.util.Try

@ImplementedBy(classOf[JsonDataSchemaParser])
trait DataSchemaParser {

  def parse(value: String): Try[DataSchema]

}
