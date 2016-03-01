package com.persona.service.authentication.google

import com.persona.service.account.Account

sealed class AccountNotAvailableException extends RuntimeException

sealed trait GoogleAuthenticationResult {

  def validToken: Boolean

  def validAccount: Boolean

  def account: Account = throw new AccountNotAvailableException

}

sealed class InvalidTokenAndAccountResult extends GoogleAuthenticationResult {

  def validToken: Boolean = false

  def validAccount: Boolean = false

}

object InvalidTokenAndAccountResult {

  def apply(): InvalidTokenAndAccountResult = new InvalidTokenAndAccountResult

}

sealed class ValidTokenInvalidAccountResult extends GoogleAuthenticationResult {

  def validToken: Boolean = true

  def validAccount: Boolean = false

}

object ValidTokenInvalidAccountResult {

  def apply(): ValidTokenInvalidAccountResult = new ValidTokenInvalidAccountResult

}

sealed class ValidTokenAndAccountResult(acc: Account) extends GoogleAuthenticationResult {

  def validToken: Boolean = true

  def validAccount: Boolean = true

  override def account: Account = acc

}

object ValidTokenAndAccountResult {

  def apply(acc: Account): ValidTokenAndAccountResult = new ValidTokenAndAccountResult(acc)

}
