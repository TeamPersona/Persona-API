package com.persona.service.bank

import com.persona.service.account.Account
import com.websudos.phantom.dsl._

import scala.concurrent.{ExecutionContext, Future}

trait BankDAO {

  def listInformation(account: Account)(implicit ec: ExecutionContext): Future[Seq[DataItem]]

  def saveInformation(account: Account, dataItem: DataItem)(implicit ec: ExecutionContext): Future[ResultSet]

}
