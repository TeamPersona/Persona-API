package persona.api.account.personal

import com.google.inject.ImplementedBy
import persona.util.ParseError

import scalaz.ValidationNel

@ImplementedBy(classOf[JsonDataSchemaParser])
trait DataSchemaParser {

  def parse(value: String): ValidationNel[ParseError, DataSchema]

}
