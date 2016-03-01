package com.persona.service.bank

import com.datastax.driver.core.Row

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.column.CounterColumn
import com.websudos.phantom.dsl._
import com.websudos.phantom.keys.PartitionKey

sealed class InvalidDataCountType extends RuntimeException

trait DataCountUtils {

  def toDataType(category: String, subcategory: String): String = {
    category + "_" + subcategory
  }

  def fromDataType(dataType: String): (String, String) = {
    val dataTypes = dataType.split('_')

    if(2 == dataTypes.length) {
      (dataTypes(0), dataTypes(1))
    } else {
      throw new InvalidDataCountType
    }
  }

}

class DataCountsTable extends CassandraTable[DataCountsTable, DataCount] with DataCountUtils {

  override def tableName: String = "counts"

  object user_id extends IntColumn(this) with PartitionKey[Int]

  object data_type extends StringColumn(this) with PrimaryKey[String]

  object count extends CounterColumn(this)

  def fromRow(row: Row): DataCount = {
    val (category, subcategory) = fromDataType(data_type(row))

    DataCount(
      category,
      subcategory,
      count(row)
    )
  }

}
