package com.persona.service.bank

import com.persona.service.account.Account

import com.websudos.phantom.dsl.{context => _, _}

import scala.concurrent.{ExecutionContext, Future}

trait BankDAO {

  def retrieve(account: Account)(implicit ec: ExecutionContext): Future[List[DataItem]]

  def insert(account: Account, dataItem: DataItem)(implicit ec: ExecutionContext): Future[ResultSet]

  def has(account: Account, data: List[(String, String)])(implicit ec: ExecutionContext): Future[Boolean]

  def has(account: Account, category: String, subcategory: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    has(account, List((category, subcategory)))
  }

}
