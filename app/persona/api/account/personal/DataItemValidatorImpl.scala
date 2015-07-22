package persona.api.account.personal

import javax.inject.{Inject, Singleton}

import scalaz.Scalaz._
import scalaz.ValidationNel

// This class should be a singleton, since it loads data from disk.  Shouldn't load data multiple times
@Singleton
class DataItemValidatorImpl @Inject() (
  dataSchemaLoader: DataSchemaLoader) extends DataItemValidator {

  private[this] val schemas = loadSchemas()

  def validate(item: DataItem): ValidationNel[DataItemValidationError, DataItem] = {
    getSchema(item) map { schema =>
      schema.validate(item)
    } getOrElse {
      new InvalidCategoryError(item.category, item.subcategory).failureNel
    }
  }

  private[this] def getSchema(item: DataItem): Option[DataSchema] = {
    val maybeCategoryMap = schemas.get(item.category)

    maybeCategoryMap flatMap { categoryMap =>
      categoryMap.get(item.subcategory)
    }
  }

  private[this] def loadSchemas(): Map[String, Map[String, DataSchema]] = {
    val dataSchemas = dataSchemaLoader.load

    val groupedSchemas = dataSchemas map { dataSchema =>
      dataSchema.category -> Map(dataSchema.subcategory -> dataSchema)
    }

    groupedSchemas.toMap
  }

}
