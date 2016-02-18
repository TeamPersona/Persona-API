package com.persona.service.account

import scala.concurrent.{ExecutionContext, Future}

trait AccountDAO {

  def retrieve(id: Int)(implicit ec: ExecutionContext): Future[Option[Account]]

  def exists(accountDescriptor: AccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean]

  def create(accountDescriptor: AccountDescriptor, hashedPassword: String)(implicit ec: ExecutionContext): Future[Unit]

}
