package persona.api.account.personal

import java.io.{File, FileNotFoundException}
import java.nio.file.NotDirectoryException
import javax.inject.Inject

import com.google.common.io.Files
import play.api.Configuration

import scala.io.Source

class JsonDataSchemaLoader @Inject() (
  parser: DataSchemaParser,
  configuration: Configuration) extends DataSchemaLoader {

  val schemaDirName = configuration.getString("persona.data.schemaDirectory").get
  val schemaDir = new File(schemaDirName)

  if(!schemaDir.exists()) {
    throw new FileNotFoundException("Couldn't find schema directory " + schemaDir.getAbsolutePath)
  }

  if(!schemaDir.isDirectory) {
    throw new NotDirectoryException("Schema directory is not a directory: " + schemaDir.getAbsolutePath)
  }

  def load: Seq[DataSchema] =  {
    val jsonFiles = schemaDir.listFiles filter { file =>
      "json" == Files.getFileExtension(file.getName).toLowerCase
    }

    // Load files from disk in parallel
    val parallelPartitionedJsonFiles = jsonFiles.toSeq.par

    val schemas = parallelPartitionedJsonFiles map { jsonFile =>
      val jsonFileContents = Source.fromFile(jsonFile).mkString

      parser.parse(jsonFileContents).getOrElse {
        throw new InvalidSchemaException(jsonFile.getName + " is invalid")
      }
    }

    schemas.seq
  }

}
