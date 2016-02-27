package com.persona.service.authorization

import com.persona.service.account.Account
import com.persona.service.account.thirdparty.ThirdPartyAccount

import scala.concurrent.{Future, ExecutionContext}

trait RefreshTokenDAO {

  def create(refreshTokenDescriptor: RefreshTokenDescriptor)(implicit ec: ExecutionContext): Future[Unit]

  def validate(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[(Account, ThirdPartyAccount)]]

}
