package persona.api.offers.offerImpl


import java.util.UUID

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.api.offer.offerImpl.JsonOfferWriter
import play.api.libs.json._
import persona.api.offer.Offer

@RunWith(classOf[JUnitRunner])
class JsonOfferWriterSpec extends Specification with Mockito {
  "JsonOfferWriter" should {
    "write data item with one criterion" in {
      val expectedJson =
        """
          |{
          |  "id":"fb93dda6-7e1f-439c-a249-0d4c22252858",
          |  "creationDay":1437090963326,
          |  "description":"desc",
          |  "expirationTime":1437090963326,
          |  "currentParticipants":2,
          |  "maxParticipants":10,
          |  "value":3.50,
          |  "criteria":{
          |      "criterionCategory":"crit"
          |    }
          |}
        """.stripMargin

      val offer = mock[Offer]
      offer.id returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")
      offer.creationDay returns new DateTime(1437090963326L)
      offer.description returns "desc"
      offer.expirationTime returns new DateTime(1437090963326L)
      offer.currentParticipants returns 2
      offer.maxParticipants returns 10
      offer.value returns 3.5
      offer.criteria returns Map("criterionCategory" -> "crit")

      new JsonOfferWriter().toJson(offer) mustEqual Json.parse(expectedJson)
    }

    "write data item with multiple criteria" in {
      val expectedJson =
        """
          |{
          |  "id":"fb93dda6-7e1f-439c-a249-0d4c22252858",
          |  "creationDay":1437090963326,
          |  "description":"desc",
          |  "expirationTime":1437090963326,
          |  "currentParticipants":2,
          |  "maxParticipants":10,
          |  "value":3.50,
          |  "criteria":{
          |      "criterionCategory":"crit",
          |      "criterionCategory2":"crit2"
          |    }
          |}
        """.stripMargin

      val offer = mock[Offer]
      offer.id returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")
      offer.creationDay returns new DateTime(1437090963326L)
      offer.description returns "desc"
      offer.expirationTime returns new DateTime(1437090963326L)
      offer.currentParticipants returns 2
      offer.maxParticipants returns 10
      offer.value returns 3.5
      offer.criteria returns Map("criterionCategory" -> "crit", "criterionCategory2" -> "crit2")

      new JsonOfferWriter().toJson(offer) mustEqual Json.parse(expectedJson)
    }

  }
}
