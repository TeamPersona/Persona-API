package persona.api.authentication

import play.mvc.Result

sealed trait AuthenticationResult

object AuthenticationResult {

  /**
   * A user is denied access due to invalid credentials
   */
  case class Failed() extends AuthenticationResult

  /**
   * @todo Redirection to external provider page (Facebook)
   *
   * @param result to extract redirection information
   */
  case class Redirection(result: Result) extends AuthenticationResult

  /**
   * Successfully authenticated
   *
   * @param profile authenticated user profile
   */
  case class Authenticated(profile: Profile) extends AuthenticationResult

}
