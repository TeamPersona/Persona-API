package com.persona.service.account.google

import scala.concurrent.{ExecutionContext, Future}

trait GoogleAccountDAO {

  def exists(googleId: String)(implicit ec: ExecutionContext): Future[Boolean]

  def exists(googleAccountDescriptor: GoogleAccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean]

  def create(googleAccountDescriptor: GoogleAccountDescriptor)(implicit ec: ExecutionContext): Future[Unit]

}
