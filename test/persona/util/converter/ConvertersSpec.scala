package persona.util.converter

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.util.converter.Converters._

@RunWith(classOf[JUnitRunner])
class ConvertersSpec extends Specification {

  "Converters" should {
    "convert valid int" in {
      val tryInt = new IntConverter().convert("123")
      tryInt.isSuccess must beTrue
      tryInt.get mustEqual 123
    }

    "throw exception for invalid int" in {
      val tryInt = new IntConverter().convert("abc")
      tryInt.isFailure must beTrue
      tryInt.get must throwA[NumberFormatException]
    }

    "convert valid long" in {
      val longValue: Long = Int.MaxValue + 1L
      val tryLong = new LongConverter().convert(longValue.toString)
      tryLong.isSuccess must beTrue
      tryLong.get mustEqual longValue
    }

    "throw exception for invalid long" in {
      val tryLong = new LongConverter().convert("abc")
      tryLong.isFailure must beTrue
      tryLong.get must throwA[NumberFormatException]
    }

    "convert string" in {
      val tryString = new StringConverter().convert("abc")
      tryString.isSuccess must beTrue
      tryString.get mustEqual "abc"
    }
  }

}