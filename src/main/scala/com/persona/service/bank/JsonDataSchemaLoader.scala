package com.persona.service.bank

import java.io.{File, FileNotFoundException}
import java.nio.file.NotDirectoryException

import com.google.common.io.Files
import com.typesafe.config.Config

import scala.io.Source

import spray.json._

class JsonDataSchemaLoader(schemaLocation: String) extends DataSchemaLoader with DataSchemaJsonProtocol {

  val schemaDir = new File(schemaLocation)

  if(!schemaDir.exists()) {
    throw new FileNotFoundException("Couldn't find schema directory " + schemaDir.getAbsolutePath)
  }

  if(!schemaDir.isDirectory) {
    throw new NotDirectoryException("Schema directory is not a directory: " + schemaDir.getAbsolutePath)
  }

  def load(): Seq[DataSchema] =  {
    val jsonFiles = schemaDir.listFiles.filter { file =>
      "json" == Files.getFileExtension(file.getName).toLowerCase
    }

    // Load files from disk in parallel
    val schemas = jsonFiles.toSeq.par.map { jsonFile =>
      val fileContents = Source.fromFile(jsonFile).mkString

      fileContents.parseJson.convertTo[DataSchema]
    }

    schemas.seq
  }

}
