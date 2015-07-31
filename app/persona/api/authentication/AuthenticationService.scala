package persona.api.authentication

import com.google.inject.ImplementedBy
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import persona.model.authentication.User

import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService extends IdentityService[User] {
  /**
   * Saves a user
   *
   * @param user user to save
   * @return saved user
   */
  def save(user: User): Future[User]

  /**
   * Save profile for a user
   *
   * @param profile profile to save
   * @return updated user
   */
  def save(profile: CommonSocialProfile): Future[User]
}
