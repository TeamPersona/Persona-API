package com.persona.service.authorization

case class RefreshTokenDescriptor(
  token: String,
  accountId: Int,
  thirdPartyAccountId: String,
  valid: Boolean = true
)
