package persona.api.offers

import java.util.UUID

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.runner.JUnitRunner
import persona.api.offer.offerImpl._
import play.api.test.PlaySpecification
import persona.api.offer._
import scala.concurrent.{ExecutionContext, Future}

@RunWith(classOf[JUnitRunner])
class OfferServiceImplSpec extends PlaySpecification with Mockito {

  "OfferServiceImpl" should {
    "list valid offer" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      val offer = mock[Offer]
      offer.id returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")
      offer.creationDay returns new DateTime(1437090963326L)
      offer.description returns "desc"
      offer.expirationTime returns new DateTime(1437090963326L)
      offer.currentParticipants returns 2
      offer.maxParticipants returns 10
      offer.value returns 3.5

      val offerDao = mock[OfferDAO]
      offerDao.list(any[ExecutionContext]) returns Future.successful(Seq(offer))

      val dataSchema = mock[OfferSchema]
      dataSchema.creationDay returns new DateTime(1437090963326L)
      dataSchema.description returns "desc"
      dataSchema.expirationTime returns new DateTime(1437090963326L)
      dataSchema.currentParticipants returns 2
      dataSchema.maxParticipants returns 10
      dataSchema.value returns 3.50
      dataSchema.validate(any[Offer]) returns true

      val offerService = new OfferServiceImpl(offerDao)

      val futureInformation = offerService.list

      val information = await(futureInformation)
      information must have size 1
      information.head.id mustEqual offer.id
    }
    //TODO: get powermock to mock UUID
//    "get valid offer" in {
//      import scala.concurrent.ExecutionContext.Implicits.global
//
//      val offer = mock[Offer]
//      offer.id returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")
//      offer.creationDay returns new DateTime(1437090963326L)
//      offer.description returns "desc"
//      offer.expirationTime returns new DateTime(1437090963326L)
//      offer.currentParticipants returns 2
//      offer.maxParticipants returns 10
//      offer.value returns 3.5
//
//      val offerDao = mock[OfferDAO]
//      offerDao.get()(any[ExecutionContext]) returns Future.successful(Option(offer))
//
//      val dataSchema = mock[OfferSchema]
//      dataSchema.creationDay returns new DateTime(1437090963326L)
//      dataSchema.description returns "desc"
//      dataSchema.expirationTime returns new DateTime(1437090963326L)
//      dataSchema.currentParticipants returns 2
//      dataSchema.maxParticipants returns 10
//      dataSchema.value returns 3.50
//      dataSchema.validate(any[Offer]) returns true
//
//      val offerService = new OfferServiceImpl(offerDao)
//
//      val futureInformation = offerService.get(mock[UUID])
//
//      val information = await(futureInformation)
//      information.head.id mustEqual offer.id
//    }
  }
}
