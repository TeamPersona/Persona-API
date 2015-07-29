package persona.api.offer

import java.util.UUID
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
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
    val NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L
    val epoch = (id.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000
    val date = new DateTime(epoch)
    val fmt = DateTimeFormat.forPattern("YYYY-MM-dd")
    val dateStr = fmt.print(date)
    val futureData = offerDAO.get(id, dateStr)
    futureData
  }
}
