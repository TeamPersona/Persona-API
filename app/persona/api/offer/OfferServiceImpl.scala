package persona.api.offer

import scala.concurrent.{ExecutionContext, Future}

class OfferServiceImpl extends OfferService {

  def list: Future[Option[Seq[Offer]]] = ???

    // TODO: when get SEQ back from the DAO, it should only be one or none (but it's in a seq) so convert to single Offer
  def get(id: String): Future[Option[Offer]] = ???
}
