package persona.api.account.personal

import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[FileDataSchemaLoader])
trait DataSchemaLoader {
  def load: Future[Seq[DataSchema]]
}
