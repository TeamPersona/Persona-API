package persona.api.account.personal

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FieldDescriptorSpec extends Specification {

  "FieldDescriptor" should {
    "support integer types" in {
      FieldDescriptor("name", true, "int") must not(throwAn[Exception])
    }

    "support long types" in {
      FieldDescriptor("name", true, "long") must not(throwAn[Exception])
    }

    "support string types" in {
      FieldDescriptor("name", false, "string") must not(throwAn[Exception])
    }

    "support upper case types" in {
      FieldDescriptor("name", false, "INT") must not(throwAn[Exception])
    }

    "throw exception for unknown type" in {
      FieldDescriptor("name", false, "unknown_type") must throwAn[IllegalArgumentException]
    }

    "validate data" in {
      val validation = FieldDescriptor("name", true, "int").validate("123")
      validation.isSuccess must beTrue
    }

    "validate invalid data" in {
      val validation = FieldDescriptor("name", true, "int").validate("abc")

      validation.disjunction.leftMap { dataItemValidationErrors =>
        dataItemValidationErrors.size mustEqual 1
        dataItemValidationErrors.head must beAnInstanceOf[TypeMismatchError]
      }

      validation.isSuccess must beFalse
    }
  }

}