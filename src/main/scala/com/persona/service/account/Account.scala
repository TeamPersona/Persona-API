package com.persona.service.account

case class AccountDescriptor(
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String)

case class Account(
  id: Int,
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String)
