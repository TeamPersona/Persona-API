package persona.api.offer

import java.util.UUID

import scala.concurrent.Future

class OfferServiceImpl extends OfferService {
  def list: Future[Option[Seq[Offer]]] = ???

  def get(id: UUID): Future[Option[Offer]] = ???
}
