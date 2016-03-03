package com.persona.service.authorization

case class AuthorizationCode(
  code: String,
  accountId: Int,
  thirdPartyAccountId: String,
  valid: Boolean = true
)
