package com.persona.service.bank

import com.persona.service.account.Account
import com.persona.util.db.PersonaCassandraConnector

import com.websudos.phantom.dsl.{context => _, _}

import scala.concurrent.{ExecutionContext, Future}

class DataCountsDAO extends DataCountsTable with PersonaCassandraConnector with DataCountUtils {

  def counts(account: Account, desiredCounts: List[(String, String)])(implicit ec: ExecutionContext): Future[List[DataCount]] = {
    val convertedDesiredCounts = desiredCounts.map { desiredCount =>
      val (category, subcategory) = desiredCount

      toDataType(category, subcategory)
    }

    select.where(counts => counts.user_id eqs account.id)
          .and(counts => counts.data_type in convertedDesiredCounts)
          .fetch()
  }

  def increment(account: Account, category: String, subcategory: String)(implicit ec: ExecutionContext): Future[ResultSet] = {
    val dataType = toDataType(category, subcategory)

    update.where(counts => counts.user_id eqs account.id)
          .and(counts => counts.data_type eqs dataType)
          .modify(counts => counts.count increment 1)
          .future()
  }

}
