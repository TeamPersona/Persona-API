package com.persona.service.account.google

import com.persona.service.account.Account

import scala.concurrent.{ExecutionContext, Future}

trait GoogleAccountDAO {

  def retrieve(googleId: String)(implicit ec: ExecutionContext): Future[Option[Account]]

  def exists(googleAccountDescriptor: GoogleAccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean]

  def create(googleAccountDescriptor: GoogleAccountDescriptor)(implicit ec: ExecutionContext): Future[Account]

}
