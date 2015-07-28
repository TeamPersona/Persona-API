package persona.api.account.personal

import com.google.inject.ImplementedBy
import persona.api.authentication.User

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CassandraPersonalDataDAO])
trait PersonalDataDAO {

  def listInformation(user: User)(implicit ec: ExecutionContext): Future[Seq[DataItem]]

}
