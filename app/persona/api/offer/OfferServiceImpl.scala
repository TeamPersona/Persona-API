package persona.api.offer

import java.util.UUID
import persona.api.offer.offerImpl._
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class OfferServiceImpl @Inject() (offerDAO: OfferDAO) extends OfferService {

  //TODO: get offerSchema object so we can check validity

  def list(implicit ec: ExecutionContext): Future[Seq[Offer]] = {

    val futureData = offerDAO.list

    futureData map { offers =>
      offers.foreach { offer =>
          //TODO: check validity
      }
      offers
    }

  }

  def get(id: UUID)(implicit ec: ExecutionContext): Future[Option[Offer]] = {
    val futureData = offerDAO.get(id)
    futureData
  }
}
