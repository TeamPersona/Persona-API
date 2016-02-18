package com.persona.service.account

import com.persona.util.PersonaError

sealed trait AccountValidationError extends PersonaError

sealed class InvalidEmailAddressError extends AccountValidationError {

  def errorMessage: String = "Invalid email address"

}

sealed class InvalidPhoneNumberError extends AccountValidationError {

  def errorMessage: String = "Invalid phone number"

}

sealed class AccountAlreadyExistsError extends AccountValidationError {

  def errorMessage: String = "Account already exists"

}
