package com.persona.service.bank

import com.persona.service.account.Account
import com.persona.util.db.PersonaCassandraConnector

import com.websudos.phantom.dsl.{context => _, _}

import scala.concurrent.{Future, ExecutionContext}

class DataItemsDAO extends DataItemsTable with PersonaCassandraConnector {

  def retrieve(account: Account)(implicit ec: ExecutionContext): Future[List[DataItem]] = {
    select.where(_.user_id eqs account.id)
          .fetch
  }

  def insert(account: Account, dataItem: DataItem)(implicit ec: ExecutionContext): Future[ResultSet] = {
    insert().value(_.user_id, account.id)
            .value(_.creation_time, dataItem.creationTime)
            .value(_.category, dataItem.category)
            .value(_.subcategory, dataItem.subcategory)
            .value(_.data, dataItem.data)
            .future()
  }

}
