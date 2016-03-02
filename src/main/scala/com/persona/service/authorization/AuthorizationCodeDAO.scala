package com.persona.service.authorization

import com.persona.service.account.Account
import com.persona.service.account.thirdparty.ThirdPartyAccount

import scala.concurrent.{Future, ExecutionContext}

trait AuthorizationCodeDAO {

  def create(authorizationCode: AuthorizationCode)(implicit ec: ExecutionContext): Future[Unit]

  def validate(code: String)(implicit ec: ExecutionContext): Future[Option[(Account, ThirdPartyAccount)]]

  def invalidate(authorizationCode: AuthorizationCode)(implicit ec: ExecutionContext): Future[Unit]

}
