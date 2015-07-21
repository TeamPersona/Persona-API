package persona.api.offer

import com.google.inject.ImplementedBy
import java.util.UUID

import scala.concurrent.Future

@ImplementedBy(classOf[OfferServiceImpl])
trait OfferService {
  def list: Future[Option[Seq[Offer]]]
  def get(id: UUID): Future[Option[Offer]]
}
