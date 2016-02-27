package com.persona.service.authorization

case class AuthorizationResult(
  accessToken: String,
  expirationTime: Int,
  refreshToken: Option[String] = None,
  tokenType: String = "Bearer"
)
