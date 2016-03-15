package com.persona.service.bank

import com.persona.service.account.Account

import com.websudos.phantom.dsl.ResultSet

import scala.concurrent.{Future, ExecutionContext}

class CassandraBankDAO(dataItemsDAO: DataItemsDAO, dataCountsDAO: DataCountsDAO) extends BankDAO {

  def retrieve(account: Account)(implicit ec: ExecutionContext): Future[List[DataItem]] = {
    dataItemsDAO.retrieve(account)
  }

  def insert(account: Account, dataItem: DataItem)(implicit ec: ExecutionContext): Future[ResultSet] = {
    val futureInsertionResult = dataItemsDAO.insert(account, dataItem)
    val futureCountIncrementResult = dataCountsDAO.increment(account, dataItem.category, dataItem.subcategory)

    for {
      insertionResult <- futureInsertionResult
      _ <- futureCountIncrementResult
    } yield insertionResult
  }

  def has(account: Account, data: List[(String, String)])(implicit ec: ExecutionContext): Future[List[Boolean]] = {
    dataCountsDAO.counts(account, data).map { countsList =>
      val counts = countsList.map(count => (count.category, (count.subcategory, count))).groupBy(_._1).mapValues(_.map(_._2).toMap)

      val results = data.par.map { item =>
        val (category, subcategory) = item
        val dataCountOption = counts.get(category).flatMap { subcategories =>
          subcategories.get(subcategory)
        }

        dataCountOption match {
          case Some(dataCount) =>
            dataCount.count > 0

          case None =>
            false
        }
      }

      results.toList
    }
  }

}
