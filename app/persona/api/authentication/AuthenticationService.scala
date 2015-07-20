package persona.api.authentication

import com.google.inject.ImplementedBy
import play.api.mvc.RequestHeader

import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {

  /**
   * Find a profile that matches the userId
   *
   * @param userId user ID
   * @param providerId provider ID
   * @return profile if found
   */
  def find(providerId: String, userId: String): Future[Option[BaseProfile]]

  /**
   * Returns password information given a user
   *
   * @param user
   * @return password information if found
   */
  def getPasswordInfo(user: User): Future[Option[Password]]

  /**
   * Update password information given a user
   *
   * @param user
   * @param password
   * @return optional user profile
   */
  def updatePasswordInfo(user: User, password: Password): Future[Option[BaseProfile]]

}
