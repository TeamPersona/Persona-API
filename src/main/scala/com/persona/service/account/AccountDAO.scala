package com.persona.service.account

import scala.concurrent.{ExecutionContext, Future}

trait AccountDAO {

  def retrievePassword(email: String)(implicit ec: ExecutionContext): Future[Option[String]]

  def exists(accountDescriptor: AccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean]

  def create(accountDescriptor: AccountDescriptor, hashedPassword: String)(implicit ec: ExecutionContext): Future[Account]

}
