package com.persona.service.account

case class AccountDescriptor(
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String) {

  def toRawAccount: RawAccount = RawAccount(None, givenName, familyName, emailAddress, phoneNumber)

}

sealed class InvalidAccountOperationException extends RuntimeException

case class RawAccount(
  id: Option[Int],
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String) {

  def toAccount: Account = {
    id.map { accountId =>
      Account(accountId, givenName, familyName, emailAddress, phoneNumber)
    } getOrElse {
      throw new InvalidAccountOperationException
    }
  }

}

case class Account(
  id: Int,
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String) {

  def toRawAccount: RawAccount = RawAccount(Some(id), givenName, familyName, emailAddress, phoneNumber)

}
