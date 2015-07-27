package persona.api.offers.offerImpl

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.api.offer.offerImpl._
import persona.util.ParseException

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

            val tryDataSchema = new JsonOfferParser().parse(json)
            tryDataSchema.isSuccess must beTrue

            val dataSchema = tryDataSchema.get
            dataSchema.creationDay mustEqual new DateTime(1437090963326L)
            dataSchema.description mustEqual "desc"
            dataSchema.expirationTime mustEqual new DateTime(1437090963326L)
            dataSchema.currentParticipants mustEqual 2
            dataSchema.maxParticipants mustEqual 10
            dataSchema.value mustEqual 3.50
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

      val tryDataSchema = new JsonOfferParser().parse(json)
      tryDataSchema.isSuccess must beTrue

      val dataSchema = tryDataSchema.get
      dataSchema.creationDay mustEqual new DateTime(1437090963326L)
      dataSchema.description mustEqual "desc"
      dataSchema.expirationTime mustEqual new DateTime(1437090963326L)
      dataSchema.currentParticipants mustEqual 2
      dataSchema.maxParticipants mustEqual 10
      dataSchema.value mustEqual 3.50
    }

    "throw exception for invalid json" in {
      val json = "{"

      val tryDataSchema = new JsonOfferParser().parse(json)
      tryDataSchema.isFailure must beTrue
      tryDataSchema.get must throwAn[Exception]
    }

    "throw exception for json missing required field" in {
      val json =
        """
          |{
          |  "creationDay":"test"
          |}
        """.stripMargin

      val tryDataSchema = new JsonOfferParser().parse(json)
      tryDataSchema.isFailure must beTrue
      tryDataSchema.get must throwA[ParseException]
    }
  }
}
