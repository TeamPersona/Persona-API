package com.persona.service.authorization

case class AuthorizationResult(
  accessToken: String,
  refreshToken: Option[String],
  expirationTime: Int,
  tokenType: String
)
