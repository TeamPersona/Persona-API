package com.persona.service.account.google

import com.persona.util.PersonaError

sealed trait GoogleAccountValidationError extends PersonaError

sealed class InvalidIdTokenError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token is invalid"

}

sealed class GoogleAccountAlreadyExistsError extends GoogleAccountValidationError {

  def errorMessage: String = "Account already exists"

}

sealed class BadClaimsFormatError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token claims are malformed"

}

sealed class MissingSubjectClaimError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token missing subject claim"

}

sealed class MissingEmailClaimError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token missing email claim"

}

sealed class MissingEmailVerifiedClaimError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token missing email_verified claim"

}

sealed class EmailNotVerifiedError extends GoogleAccountValidationError {

  def errorMessage: String = "Email is not verified"

}

sealed class MissingGivenNameClaimError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token missing given_name claim"

}

sealed class MissingFamilyNameClaimError extends GoogleAccountValidationError {

  def errorMessage: String = "ID token missing family_name claim"

}
