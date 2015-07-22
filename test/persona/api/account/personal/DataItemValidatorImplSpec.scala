package persona.api.account.personal

import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scalaz.Scalaz._

@RunWith(classOf[JUnitRunner])
class DataItemValidatorImplSpec extends Specification with Mockito {

  "DataItemValidator" should {
    "validate valid data item" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.category returns "category"
      mockDataItem.subcategory returns "subcategory"

      val mockDataSchema = mock[DataSchema]
      mockDataSchema.category returns "category"
      mockDataSchema.subcategory returns "subcategory"
      mockDataSchema.validate(any[DataItem]) returns mockDataItem.successNel

      val mockDataSchemaLoader = mock[DataSchemaLoader]
      mockDataSchemaLoader.load returns Seq(mockDataSchema)

      val validation = new DataItemValidatorImpl(mockDataSchemaLoader).validate(mockDataItem)
      validation.isSuccess must beTrue

      val validatedDataItem = validation.toOption.get
      validatedDataItem.category mustEqual mockDataItem.category
    }

    "validate data item having bad category" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.category returns "wrong_category"
      mockDataItem.subcategory returns "subcategory"

      val mockDataSchema = mock[DataSchema]
      mockDataSchema.category returns "category"
      mockDataSchema.subcategory returns "subcategory"

      val mockDataSchemaLoader = mock[DataSchemaLoader]
      mockDataSchemaLoader.load returns Seq(mockDataSchema)

      val validation = new DataItemValidatorImpl(mockDataSchemaLoader).validate(mockDataItem)

      validation.disjunction.leftMap { dataItemValidationErrors =>
        dataItemValidationErrors.size mustEqual 1
        dataItemValidationErrors.head must beAnInstanceOf[InvalidCategoryError]
      }

      validation.isSuccess must beFalse
    }

    "validate data item having bad subcategory" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.category returns "category"
      mockDataItem.subcategory returns "wrong_subcategory"

      val mockDataSchema = mock[DataSchema]
      mockDataSchema.category returns "category"
      mockDataSchema.subcategory returns "subcategory"

      val mockDataSchemaLoader = mock[DataSchemaLoader]
      mockDataSchemaLoader.load returns Seq(mockDataSchema)

      val validation = new DataItemValidatorImpl(mockDataSchemaLoader).validate(mockDataItem)

      validation.disjunction.leftMap { dataItemValidationErrors =>
        dataItemValidationErrors.size mustEqual 1
        dataItemValidationErrors.head must beAnInstanceOf[InvalidCategoryError]
      }

      validation.isSuccess must beFalse
    }
  }

}
