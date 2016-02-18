package com.persona.service.account

import scalaz.Scalaz._
import scalaz.ValidationNel

object AccountValidator {

  private val Rfc2822 = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?".r
  private val SimplePhoneNumber = """\([0-9]{3}\) [0-9]{3}-[0-9]{4}""".r

}

class AccountValidator {

  def validate(accountDescriptor: AccountDescriptor): ValidationNel[AccountValidationError, AccountDescriptor] = {
    (hasValidEmailAddress(accountDescriptor) |@| hasValidPhoneNumber(accountDescriptor)) { (_, _) =>
      accountDescriptor
    }
  }

  private[this] def hasValidEmailAddress(accountDescriptor: AccountDescriptor): ValidationNel[AccountValidationError, AccountDescriptor] = {
    accountDescriptor.emailAddress match {
      case AccountValidator.Rfc2822() => accountDescriptor.successNel
      case _ => (new InvalidEmailAddressError).failureNel
    }
  }

  private[this] def hasValidPhoneNumber(accountDescriptor: AccountDescriptor): ValidationNel[AccountValidationError, AccountDescriptor] = {
    accountDescriptor.phoneNumber match {
      case AccountValidator.SimplePhoneNumber() => accountDescriptor.successNel
      case _ => (new InvalidPhoneNumberError).failureNel
    }
  }

}
