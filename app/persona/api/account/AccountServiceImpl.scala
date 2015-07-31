package persona.api.account

import javax.inject.Inject

import com.websudos.phantom.dsl.ResultSet
import persona.api.account.personal._
import persona.model.authentication.User

import scala.concurrent.{ExecutionContext, Future}
import scalaz._

class AccountServiceImpl @Inject() (
  personalDataDAO: PersonalDataDAO,
  dataItemValidator: DataItemValidator) extends AccountService {

  def listInformation(user: User)(implicit ec: ExecutionContext): Future[Seq[DataItem]] = {
    val futureData = personalDataDAO.listInformation(user)

    futureData map { data =>
      data.foreach { dataItem =>
        // We're retrieving data that's already been stored, so it should be valid data
        // If validation fails, this will throw an exception
        dataItemValidator.ensureValid(dataItem)
      }

      // If validation passes, then return the data
      data
    }
  }

  def saveInformation(dataItem: DataItem)(implicit ec: ExecutionContext): ValidationNel[DataItemValidationError, Future[ResultSet]] = {
    val validationResult = dataItemValidator.validate(dataItem)

    validationResult.map { item =>
      personalDataDAO.saveInformation(item)
    }
  }
}
