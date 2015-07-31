package persona.api.account

import com.google.inject.ImplementedBy
import com.websudos.phantom.dsl.ResultSet
import persona.api.account.personal.{DataItem, DataItemValidationError}
import persona.model.authentication.User

import scala.concurrent.{ExecutionContext, Future}
import scalaz.ValidationNel

@ImplementedBy(classOf[AccountServiceImpl])
trait AccountService {

  def listInformation(user: User)(implicit ec: ExecutionContext): Future[Seq[DataItem]]

  def saveInformation(dataItem: DataItem)(implicit ec: ExecutionContext): ValidationNel[DataItemValidationError, Future[ResultSet]]

}
