package com.persona.service.bank

import java.util.UUID

import com.datastax.driver.core.Row
import com.persona.service.account.Account
import com.persona.util.db.PersonaCassandraConnector
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl.{context => _, _}
import com.websudos.phantom.keys.PartitionKey

import scala.concurrent.{ExecutionContext, Future}

class BankTable extends CassandraTable[BankTable, DataItem] {

  override def tableName = "bank"

  def fromRow(row: Row): DataItem = {
    DataItem(
      creation_time(row),
      category(row),
      subcategory(row),
      data(row)
    )
  }

  object user_id extends UUIDColumn(this) with PartitionKey[UUID]

  object creation_time extends DateTimeColumn(this) with ClusteringOrder[DateTime] with Descending

  object category extends StringColumn(this)

  object subcategory extends StringColumn(this)

  object data extends MapColumn[BankTable, DataItem, String, String](this)

}

class CassandraBankDAO extends BankTable with BankDAO with PersonaCassandraConnector {

  def listInformation(account: Account)(implicit ec: ExecutionContext): Future[Seq[DataItem]] = {
    select.where(_.user_id eqs account.id)
      .fetch
      .map(_.toSeq)
  }

  def saveInformation(account: Account, dataItem: DataItem)
                     (implicit ec: ExecutionContext): Future[ResultSet] = {
    insert.value(_.user_id, account.id)
          .value(_.creation_time, dataItem.creationTime)
          .value(_.category, dataItem.category)
          .value(_.subcategory, dataItem.subcategory)
          .value(_.data, dataItem.data)
          .future()
  }

}
