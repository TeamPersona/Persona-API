package persona.controllers

import javax.inject.{Inject, Singleton}

import persona.api.authentication.AuthenticationService
import play.api.mvc.Controller

@Singleton
class Authentication @Inject() (authenticationService: AuthenticationService ) extends Controller {

  def authorize = TODO

}
