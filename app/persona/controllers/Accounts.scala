package persona.controllers

import javax.inject.{Inject, Singleton}

import persona.api.account.AccountService
import play.api.mvc.Controller

@Singleton
class Accounts @Inject() (accountService: AccountService) extends Controller {

  def listInformation = TODO

}
