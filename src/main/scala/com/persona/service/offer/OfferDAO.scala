package com.persona.service.offer

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

trait OfferDAO {

  def list()(implicit ec: ExecutionContext): Future[Seq[Offer]]

  def get(id: UUID, creationDay: String)(implicit ec: ExecutionContext): Future[Option[Offer]]

}
