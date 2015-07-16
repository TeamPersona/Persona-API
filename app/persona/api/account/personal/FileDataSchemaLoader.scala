package persona.api.account.personal

import java.io.File
import java.nio.file.NotDirectoryException

import com.google.common.io.Files

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class FileDataSchemaLoader(
  directory: String,
  parser: DataSchemaParser)(
  implicit executionContext: ExecutionContext) extends DataSchemaLoader {

  def load: Future[Seq[DataSchema]] = Future {
    val dir = new File(directory)

    if(!dir.isDirectory) {
      throw new NotDirectoryException(directory)
    }

    val jsonFiles = dir.listFiles.filter(file => "json" == Files.getFileExtension(file.getName).toLowerCase)

    val schemas = jsonFiles.map { jsonFile =>
      val jsonFileContents = Source.fromFile(jsonFile).mkString

      parser.parse(jsonFileContents).get
    }

    schemas.toSeq
  }

}