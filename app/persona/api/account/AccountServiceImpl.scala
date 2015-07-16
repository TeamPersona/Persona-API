package persona.api.account

import persona.api.account.personal.DataItem
import persona.api.authentication.User

import scala.concurrent.Future

class AccountServiceImpl extends AccountService {

  def listInformation(user: User): Future[Seq[DataItem]] = ???

}
