package persona.api.offer

import com.google.inject.ImplementedBy
import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OfferServiceImpl])
trait OfferService {
  def list(implicit ec: ExecutionContext): Future[Option[Seq[Offer]]] // TODO: add option into these? Future[Option[Seq[Offer]]], Future[Option[Offer]]
  def get(id: String)(implicit ec: ExecutionContext): Future[Option[Offer]]
}
