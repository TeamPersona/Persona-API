package persona.api.offer

import scala.concurrent.Future

class OfferServiceImpl extends OfferService {
  def list: Future[Option[Seq[Offer]]] = ???

  def get(id: Long): Future[Option[Offer]] = ???
}
