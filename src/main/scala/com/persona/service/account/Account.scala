package com.persona.service.account

import spray.json.DefaultJsonProtocol

case class AccountDescriptor(
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String
)

case class Account(
  id: Int,
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String,
  rewardPoints: Int,
  balance: Int
)

trait AccountJsonProtocol extends DefaultJsonProtocol {

  implicit val accountJsonFormat = jsonFormat7(Account)

}
