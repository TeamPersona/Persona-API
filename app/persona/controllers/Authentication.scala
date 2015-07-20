package persona.controllers

import javax.inject.{Inject, Singleton}

import persona.api.authentication.{User, AuthenticationService}
import play.api.mvc.{RequestHeader, Controller}

import scala.concurrent.Future

@Singleton
class Authentication @Inject() (authenticationService: AuthenticationService) extends Controller {

  def authenticate = TODO

  /**
   * Return user in the current session
   *
   * @return user
   */
  def currentUser(implicit request: RequestHeader): Future[Option[User]] = ???

}
