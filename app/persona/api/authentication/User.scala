package persona.api.authentication

import com.google.common.hash.Hasher

trait Profile {
  def providerId: String

  def userId: String
}

trait GenericProfile extends Profile {
  def firstName: Option[String]

  def lastName: Option[String]

  def email: Option[String]

  def password: Option[Password]

  /**
   * OAuth2.0 token for delegated authentication
   *
   * @return access token
   */
  def token: Option[String]
}

case class BaseProfile(
                        providerId: String,
                        userId: String,
                        password: Option[Password],
                        firstName: Option[String] = None,
                        lastName: Option[String] = None,
                        email: Option[String] = None,
                        token: Option[String] = None
                        ) extends GenericProfile

class User(profile: BaseProfile) {
  var mainProfile: BaseProfile = profile
  var profiles: List[BaseProfile] = List(profile)
}

case class Password(hasher: Hasher, password: String, salt: Option[String] = None)
