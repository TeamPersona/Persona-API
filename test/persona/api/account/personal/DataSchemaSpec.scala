package persona.api.account.personal

import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DataSchemaSpec extends Specification with Mockito {

  "DataSchema" should {
    "throw exception if there are no fields" in {
      new DataSchema("category", "subcategory", Seq()) must throwAn[InvalidSchemaException]
    }

    "validate valid data item" in {
      val mockField = mock[FieldDescriptor]
      mockField.name returns "testField"
      mockField.isRequired returns true
      mockField.validate(anyString) returns true

      val mockDataItem = mock[DataItem]
      mockDataItem.data returns Map("testField" -> "value")

      new DataSchema("category", "subcategory", Seq(mockField)).validate(mockDataItem) must beTrue
    }

    "validate data item missing required field" in {
      val mockField = mock[FieldDescriptor]
      mockField.name returns "testField"
      mockField.isRequired returns true
      mockField.validate(anyString) returns true

      val mockDataItem = mock[DataItem]
      mockDataItem.data returns Map("notTheRightField" -> "value")

      new DataSchema("category", "subcategory", Seq(mockField)).validate(mockDataItem) must beFalse
    }
  }

}
