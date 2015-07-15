package persona.controllers

import javax.inject.{Inject, Singleton}

import persona.api.authentication.AuthenticationService
import persona.models.User
import play.api.mvc.{RequestHeader, Controller}

@Singleton
class Authentication @Inject() (authenticationService: AuthenticationService) extends Controller {

  def authorize = TODO

}

object AuthUtils {

  def getUserFromCookie(implicit req:RequestHeader) = {
    req.session.get("username").flatMap(username => User.find(username));
  }

  def getUserFromQuery(implicit req:RequestHeader) = {
    val query = req.queryString.map { case (k, v) => k -> v.mkString }
    val username = query.get("username")
    val password = query.get("password");
    (username, password) match {
      case(Some(u), Some(p)) => User.find(u).filter(user => user.checkPassword(p))
      case _ => None
    }
  }

  def getUserFromRequest(implicit req:RequestHeader):Option[User] = {
    getUserFromCookie orElse getUserFromQuery
  }

}