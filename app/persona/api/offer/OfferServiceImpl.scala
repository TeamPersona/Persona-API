package persona.api.offer

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

class OfferServiceImpl extends OfferService {

  def list(implicit ec: ExecutionContext): Future[Option[Seq[Offer]]] = ???

    // TODO: when get SEQ back from the DAO, it should only be one or none (but it's in a seq) so convert to single Offer (logic may be in offers.scala)
  def get(id: UUID)(implicit ec: ExecutionContext): Future[Option[Offer]] = ???
}
