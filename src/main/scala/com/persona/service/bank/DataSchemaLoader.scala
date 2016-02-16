package com.persona.service.bank

sealed class InvalidSchemaException(message: String) extends RuntimeException(message)

trait DataSchemaLoader {

  def load(): Seq[DataSchema]

}
