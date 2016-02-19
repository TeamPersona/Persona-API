package com.persona.service.account.thirdparty

import scala.concurrent.{Future, ExecutionContext}

trait ThirdPartyAccountDAO {

  def exists(clientId: String)(implicit ec: ExecutionContext): Future[Boolean]

  def retrieve(clientId: String)(implicit ec: ExecutionContext): Future[Option[ThirdPartyAccount]]

  def create()(implicit ec: ExecutionContext): Future[ThirdPartyAccount]

}
