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
          |  "creationDay":"2006-02-28",
          |  "description":"desc",
          |  "expirationTime":"2007-03-13",
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
            dataSchema.creationDay.toString mustEqual "2006-02-28T00:00:00.000-05:00"
            dataSchema.description mustEqual "desc"
            dataSchema.expirationTime.toString mustEqual "2007-03-13T00:00:00.000-04:00"
            dataSchema.currentParticipants mustEqual 2
            dataSchema.maxParticipants mustEqual 10
            dataSchema.value mustEqual 3.50
    }

    "parse json with multiple criterion" in {
      val json =
        """
          |{
          |  "creationDay":"2006-02-28",
          |  "description":"desc",
          |  "expirationTime":"2007-03-13",
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
      dataSchema.creationDay.toString mustEqual "2006-02-28T00:00:00.000-05:00"
      dataSchema.description mustEqual "desc"
      dataSchema.expirationTime.toString mustEqual "2007-03-13T00:00:00.000-04:00"
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
