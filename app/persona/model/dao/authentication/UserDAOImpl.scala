package persona.model.dao.authentication

import java.util.UUID

import com.datastax.driver.core.Row
import com.mohiva.play.silhouette.api.LoginInfo
import com.websudos.phantom.CassandraTable
import persona.model.authentication.User

import scala.concurrent.Future

/**
 * TODO: Cassandra integration
 */
class UserLoginTable extends CassandraTable[UserLoginTable, AuthenticationDataItem] {
  override def fromRow(r: Row): AuthenticationDataItem = ???
}

class UserDAOImpl extends UserDAO {

  override def find(loginInfo: LoginInfo): Future[Option[User]] = ???

  override def save(user: User): Future[User] = ???

  override def find(userId: UUID): Future[Option[User]] = ???

}
