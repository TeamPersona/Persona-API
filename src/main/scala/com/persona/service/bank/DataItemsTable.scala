package com.persona.service.bank

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.column.{DateTimeColumn, MapColumn}
import com.websudos.phantom.dsl._
import com.websudos.phantom.keys.PartitionKey

class DataItemsTable extends CassandraTable[DataItemsTable, DataItem] {

  override def tableName: String = "data"

  object user_id extends IntColumn(this) with PartitionKey[Int]

  object creation_time extends DateTimeColumn(this) with ClusteringOrder[DateTime] with Descending

  object category extends StringColumn(this)

  object subcategory extends StringColumn(this)

  object data extends MapColumn[DataItemsTable, DataItem, String, String](this)

  def fromRow(row: Row): DataItem = {
    DataItem(
      creation_time(row),
      category(row),
      subcategory(row),
      data(row)
    )
  }

}
