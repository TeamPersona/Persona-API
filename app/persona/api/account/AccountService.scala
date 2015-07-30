package persona.api.account

import com.google.inject.ImplementedBy
import persona.api.account.personal.DataItem
import persona.model.authentication.User

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[AccountServiceImpl])
trait AccountService {

  def listInformation(user: User)(implicit ec: ExecutionContext): Future[Seq[DataItem]]

}
