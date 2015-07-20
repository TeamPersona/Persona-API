package persona.api.account.personal

import java.io.{File, FileNotFoundException}
import java.nio.file.NotDirectoryException
import javax.inject.Inject

import com.google.common.io.Files
import com.google.inject.name.Named

import scala.io.Source

class FileDataSchemaLoader @Inject() (
  @Named("DataSchemaDirectory") directory: String,
  parser: DataSchemaParser) extends DataSchemaLoader {

  def load: Seq[DataSchema] =  {
    val dir = new File(directory)

    if(!dir.exists()) {
      throw new FileNotFoundException("Couldn't find schema directory: " + directory)
    }

    if(!dir.isDirectory) {
      throw new NotDirectoryException("Schema directory is not a directory: " + directory)
    }

    val jsonFiles = dir.listFiles.filter(file => "json" == Files.getFileExtension(file.getName).toLowerCase)

    val schemas = jsonFiles.map { jsonFile =>
      val jsonFileContents = Source.fromFile(jsonFile).mkString

      parser.parse(jsonFileContents).get
    }

    schemas.toSeq
  }

}
