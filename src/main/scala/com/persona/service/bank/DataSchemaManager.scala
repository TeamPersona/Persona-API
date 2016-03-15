package com.persona.service.bank

class DataSchemaManager(dataSchemaLoader: DataSchemaLoader) {

  private[this] val schemas = loadSchemas()

  def schema(category: String, subcategory: String): Option[DataSchema] = {
    schemas.get(category).flatMap { subcategories =>
      subcategories.get(subcategory)
    }
  }

  private[this] def loadSchemas(): Map[String, Map[String, DataSchema]] = {
    val dataSchemas = dataSchemaLoader.load()

    val groupedSchemas = dataSchemas.map { dataSchema =>
      (dataSchema.category, (dataSchema.subcategory, dataSchema))
    }

    groupedSchemas.groupBy(_._1).mapValues(_.map(_._2).toMap)
  }

}
