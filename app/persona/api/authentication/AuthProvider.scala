package persona.api.authentication

import play.api.mvc.{AnyContent, Request}

import scala.concurrent.Future

abstract class AuthProvider {
  /**
   * ID for the provider
   */
  val id: String

  /**
   * Authentication Method
   *
   * i.e. OAuth2, Username/Password
   * @return
   */
  def authMethod: String

  /**
   * Return provider name
   *
   * @return provider name
   */
  override def toString = id

  def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult]
}
