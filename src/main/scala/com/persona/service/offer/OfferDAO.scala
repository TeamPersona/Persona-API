package com.persona.service.offer


import scala.concurrent.{ExecutionContext, Future}

trait OfferDAO {
  def list(lastID: Int)(implicit ec: ExecutionContext): Future[Seq[Offer]]
  def get(getId: Int)(implicit ec: ExecutionContext): Future[Option[Offer]]
}