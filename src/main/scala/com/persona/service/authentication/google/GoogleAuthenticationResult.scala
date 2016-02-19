package com.persona.service.authentication.google

sealed abstract class GoogleAuthenticationResult {

  def validToken: Boolean

  def validAccount: Boolean

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

sealed class ValidTokenAndAccountResult extends GoogleAuthenticationResult {

  def validToken: Boolean = true

  def validAccount: Boolean = true

}

object ValidTokenAndAccountResult {

  def apply(): ValidTokenAndAccountResult = new ValidTokenAndAccountResult

}
