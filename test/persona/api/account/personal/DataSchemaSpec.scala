package persona.api.account.personal

import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scalaz.Scalaz._

@RunWith(classOf[JUnitRunner])
class DataSchemaSpec extends Specification with Mockito {

  "DataSchema" should {
    "throw exception if there are no fields" in {
      new DataSchema("category", "subcategory", Seq()) must throwAn[IllegalArgumentException]
    }

    "validate valid data item" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.category returns "category"
      mockDataItem.data returns Map("testField" -> "value")

      val mockField = mock[FieldDescriptor]
      mockField.name returns "testField"
      mockField.isRequired returns true
      mockField.validate(anyString) returns "value".successNel

      val validation = new DataSchema("category", "subcategory", Seq(mockField)).validate(mockDataItem)
      validation.isSuccess must beTrue

      val validatedDataItem = validation.toOption.get
      validatedDataItem.category mustEqual mockDataItem.category
    }

    "validate data item having field not in the schema" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.data returns Map("correctField" -> "value", "unknownField" -> "value")

      val mockField = mock[FieldDescriptor]
      mockField.name returns "correctField"
      mockField.isRequired returns true
      mockField.validate(anyString) returns "value".successNel

      val validation = new DataSchema("category", "subcategory", Seq(mockField)).validate(mockDataItem)

      validation.disjunction.leftMap { dataItemValidationErrors =>
        dataItemValidationErrors.size mustEqual 1
        dataItemValidationErrors.head must beAnInstanceOf[InvalidFieldError]
      }

      validation.isSuccess must beFalse
    }

    "validate data item missing required field" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.data returns Map()

      val mockField = mock[FieldDescriptor]
      mockField.name returns "testField"
      mockField.isRequired returns true

      val validation = new DataSchema("category", "subcategory", Seq(mockField)).validate(mockDataItem)

      validation.disjunction.leftMap { dataItemValidationErrors =>
        dataItemValidationErrors.size mustEqual 1
        dataItemValidationErrors.head must beAnInstanceOf[MissingRequiredFieldError]
      }

      validation.isSuccess must beFalse
    }
  }

}
