package persona.api.account

import com.google.inject.ImplementedBy
import persona.api.account.personal.DataItem
import persona.api.authentication.User

import scala.concurrent.Future

@ImplementedBy(classOf[AccountServiceImpl])
trait AccountService {

  def listInformation(user: User): Future[Seq[DataItem]]

}
