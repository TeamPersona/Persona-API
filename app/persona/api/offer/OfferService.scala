package persona.api.offer

import com.google.inject.ImplementedBy
import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OfferServiceImpl])
trait OfferService {
  def list(implicit ec: ExecutionContext): Future[Seq[Offer]]
  def get(id: UUID)(implicit ec: ExecutionContext): Future[Option[Offer]]
}
