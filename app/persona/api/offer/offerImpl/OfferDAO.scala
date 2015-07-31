package persona.api.offer.offerImpl

import java.util.UUID

import com.google.inject.ImplementedBy
import persona.api.offer.Offer

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CassandraOfferDataDAO])
trait OfferDAO {
  def list(implicit ec: ExecutionContext): Future[Seq[Offer]]
  def get(id: UUID, creationDay: String)(implicit ec: ExecutionContext): Future[Option[Offer]]
}
