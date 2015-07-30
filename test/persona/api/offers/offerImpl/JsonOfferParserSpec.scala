package persona.api.offers.offerImpl

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.api.offer.offerImpl._
import persona.util.ValidationError

@RunWith(classOf[JUnitRunner])
class JsonOfferParserSpec extends Specification{
  "JsonOfferParser" should {
    "parse json with one criterion" in {
      val json =
        """
          |{
          |  "creationDay":1437090963326,
          |  "description":"desc",
          |  "expirationTime":1437090963326,
          |  "currentParticipants":2,
          |  "maxParticipants":10,
          |  "value":3.50,
          |  "criteria":[
          |    {
          |      "criterionCategory":"criterion1Cat",
          |      "criterion":"crit"
          |    }]
          |}
        """.stripMargin

            val tryOfferSchema = new JsonOfferParser().parse(json)
      tryOfferSchema.isSuccess must beTrue

            val offerSchema = tryOfferSchema.toOption.get
            offerSchema.creationDay mustEqual new DateTime(1437090963326L)
            offerSchema.description mustEqual "desc"
            offerSchema.expirationTime mustEqual new DateTime(1437090963326L)
            offerSchema.currentParticipants mustEqual 2
            offerSchema.maxParticipants mustEqual 10
            offerSchema.value mustEqual 3.50
    }

    "parse json with multiple criterion" in {
      val json =
        """
          |{
          |  "creationDay":1437090963326,
          |  "description":"desc",
          |  "expirationTime":1437090963326,
          |  "currentParticipants":2,
          |  "maxParticipants":10,
          |  "value":3.50,
          |  "criteria":[
          |    {
          |      "criterionCategory":"criterion1Cat",
          |      "criterion":"crit"
          |    },
          |    {
          |      "criterionCategory":"criterion2Cat",
          |      "criterion":"crit2"
          |    }]
          |}
        """.stripMargin

      val tryOfferSchema = new JsonOfferParser().parse(json)
      tryOfferSchema.isSuccess must beTrue

      val offerSchema = tryOfferSchema.toOption.get
      offerSchema.creationDay mustEqual new DateTime(1437090963326L)
      offerSchema.description mustEqual "desc"
      offerSchema.expirationTime mustEqual new DateTime(1437090963326L)
      offerSchema.currentParticipants mustEqual 2
      offerSchema.maxParticipants mustEqual 10
      offerSchema.value mustEqual 3.50
    }

    "throw exception for json missing required field" in {
      val json =
        """
          |{
          |  "creationDay":"test"
          |}
        """.stripMargin

      val tryOfferSchema = new JsonOfferParser().parse(json)

      tryOfferSchema.disjunction.leftMap { parseErrors =>
        parseErrors.size mustEqual 1
        parseErrors.head must beAnInstanceOf[ValidationError]
      }

      tryOfferSchema.isSuccess must beFalse
    }
  }
}
