package persona.api.offers.offerImpl


import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.api.offer.offerImpl._

@RunWith(classOf[JUnitRunner])
class OfferSchemaSpec extends Specification with Mockito{
  "OfferSchema" should {
    "throw exception if there are no criteria" in {
      new OfferSchema(new DateTime(1437090963326L), "desc", new DateTime(1437090963326L), 2, 10, 3.50, Seq()) must throwAn[IllegalArgumentException]
    }
  }

  //TODO: test validating Offers without other parameters
}
