package persona.api.offer.offerImpl

import java.util.UUID

import com.google.inject.ImplementedBy
import persona.api.offer.Offer

import scala.concurrent.Future

@ImplementedBy(classOf[CassandraOfferDataDAO])
trait OfferDAO {
  def list: Future[Option[Seq[Offer]]]
  def get(id: UUID): Future[Option[Offer]]
}
