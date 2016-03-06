package com.persona.service.offer


import com.persona.service.account.Account

import scala.concurrent.{ExecutionContext, Future}

trait OfferDAO {
  def list(account: Account, lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]]
  def get(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Offer]]
  def participate(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]]
  def unparticipate(account: Account, offerid: Int)(implicit ec: ExecutionContext): Future[Option[Boolean]]
}