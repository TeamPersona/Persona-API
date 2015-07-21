package persona.api.offer.offerImpl

import com.google.inject.ImplementedBy
import persona.api.offer.Offer

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CassandraOfferDataDAO])
trait OfferDAO {
  def list: Future[Seq[Offer]]
  def get(id: String): Future[Seq[Offer]]
}
