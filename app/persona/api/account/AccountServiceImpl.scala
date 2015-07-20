package persona.api.account

import javax.inject.Inject

import persona.api.account.personal._
import persona.api.authentication.User

import scala.concurrent.{ExecutionContext, Future}

class AccountServiceImpl @Inject() (
  personalDataDAO: PersonalDataDAO,
  dataSchemaLoader: DataSchemaLoader) extends AccountService {

  private[this] val schemas = loadSchemas()

  def listInformation(user: User)(implicit ec: ExecutionContext): Future[Seq[DataItem]] = {
    val futureData = personalDataDAO.listInformation(user)

    futureData map { data =>
      data.foreach { dataItem =>
        val dataSchema = getSchema(dataItem)

        val isValidData = dataSchema.exists(schema => schema.validate(dataItem))

        if(!isValidData) {
          throw new InvalidDataException("Data item does match any schemas")
        }
      }

      data
    }
  }

  private[this] def getSchema(dataItem: DataItem): Option[DataSchema] = {
    val maybeCategoryMap = schemas.get(dataItem.category)

    maybeCategoryMap.flatMap { categoryMap =>
      categoryMap.get(dataItem.subcategory)
    }
  }

  private[this] def loadSchemas(): Map[String, Map[String, DataSchema]] = {
    val dataSchemas = dataSchemaLoader.load

    val groupedSchemas = dataSchemas.map { dataSchema =>
      dataSchema.category -> Map(dataSchema.subcategory -> dataSchema)
    }

    groupedSchemas.toMap
  }

}
