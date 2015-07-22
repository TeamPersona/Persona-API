package persona.api.account

import javax.inject.Inject

import persona.api.account.personal._
import persona.api.authentication.User

import scala.concurrent.{ExecutionContext, Future}

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

      data
    }
  }

}
