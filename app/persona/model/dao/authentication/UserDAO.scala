package persona.model.dao.authentication

import java.util.UUID

import com.google.inject.ImplementedBy
import com.mohiva.play.silhouette.api.LoginInfo
import persona.model.authentication.User

import scala.concurrent.Future

@ImplementedBy(classOf[InMemoryUserDAOImpl])
trait UserDAO {

  /**
   * Find user by login information
   *
   * @param loginInfo login information of user
   * @return user if found; None if not found
   */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Find user by user ID
   *
   * @param userId ID of user
   * @return user if found; None if not found
   */
  def find(userId: UUID): Future[Option[User]]

  /**
   * Save a user
   *
   * @param user user to save
   * @return saved user
   */
  def save(user: User): Future[User]

}
