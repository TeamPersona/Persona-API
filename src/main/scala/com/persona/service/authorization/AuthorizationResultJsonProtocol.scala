package com.persona.service.authorization

import spray.json._

trait AuthorizationResultJsonProtocol {

  implicit object AuthorizationResultJsonFormat extends RootJsonFormat[AuthorizationResult] {

    private[this] def readAccessToken(authorizationResult: JsObject) = {
      authorizationResult.getFields("access_token") match {
        case Seq(JsString(accessToken)) => accessToken
        case _ => throw new DeserializationException("Invalid access token")
      }
    }

    private[this] def writeAccessToken(authorizationResult: AuthorizationResult) = {
      "access_token" -> JsString(authorizationResult.accessToken)
    }

    private[this] def readRefreshToken(authorizationResult: JsObject) = {
      authorizationResult.getFields("refresh_token") match {
        case Seq(JsString(refreshToken)) => Some(refreshToken)
        case Seq() => None
        case _ => throw new DeserializationException("Invalid refresh token")
      }
    }

    private[this] def writeRefreshToken(authorizationResult: AuthorizationResult) = {
      authorizationResult.refreshToken.map { refreshToken =>
        "refresh_token" -> JsString(refreshToken)
      }
    }

    private[this] def readExpirationTime(authorizationResult: JsObject) = {
      authorizationResult.getFields("expires_in") match {
        case Seq(JsNumber(expirationTime)) => expirationTime.toInt
        case _ => throw new DeserializationException("Invalid expiration time")
      }
    }

    private[this] def writeExpirationTime(authorizationResult: AuthorizationResult) = {
      "expires_in" -> JsNumber(authorizationResult.expirationTime)
    }

    private[this] def readTokenType(authorizationResult: JsObject) = {
      authorizationResult.getFields("token_type") match {
        case Seq(JsString("Bearer")) => "Bearer"
        case _ => throw new DeserializationException("Invalid token type")
      }
    }

    private[this] def writeTokenType(authorizationResult: AuthorizationResult) = {
      "token_type" -> JsString(authorizationResult.tokenType)
    }

    def read(authorizationResult: JsValue): AuthorizationResult = {
      val authorizationResultAsJsObject = authorizationResult.asJsObject
      val accessToken = readAccessToken(authorizationResultAsJsObject)
      val refreshToken = readRefreshToken(authorizationResultAsJsObject)
      val expirationTime = readExpirationTime(authorizationResultAsJsObject)
      val tokenType = readTokenType(authorizationResultAsJsObject)

      AuthorizationResult(accessToken, refreshToken, expirationTime, tokenType)
    }

    def write(authorizationResult: AuthorizationResult): JsValue = {
      val accessTokenJson = writeAccessToken(authorizationResult)
      val refreshTokenJsonOption = writeRefreshToken(authorizationResult)
      val expirationTimeJson = writeExpirationTime(authorizationResult)
      val tokenTypeJson = writeTokenType(authorizationResult)

      refreshTokenJsonOption.map { refreshTokenJson =>
        JsObject(
          accessTokenJson,
          refreshTokenJson,
          expirationTimeJson,
          tokenTypeJson
        )
      } getOrElse {
        JsObject(
          accessTokenJson,
          expirationTimeJson,
          tokenTypeJson
        )
      }
    }

  }

}
