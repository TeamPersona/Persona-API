package persona.api.account.personal

import java.util.UUID

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json._

@RunWith(classOf[JUnitRunner])
class JsonDataItemWriterSpec extends Specification with Mockito {

  "JsonDataItemWriter" should {
    "write data item with one field" in {
      val expectedJson =
        """
          |{
          |  "owner":"fb93dda6-7e1f-439c-a249-0d4c22252858",
          |  "creationTime":1437090963326,
          |  "category":"testCategory",
          |  "subcategory":"testSubcategory",
          |  "data": {
          |    "testField":"testValue"
          |  }
          |}
        """.stripMargin

      val dataItem = mock[DataItem]
      dataItem.ownerID returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")
      dataItem.creationTime returns new DateTime(1437090963326L)
      dataItem.category returns "testCategory"
      dataItem.subcategory returns "testSubcategory"
      dataItem.data returns Map("testField" -> "testValue")

      new JsonDataItemWriter().toJson(dataItem) mustEqual Json.parse(expectedJson)
    }

    "write data item with multiple fields" in {
      val expectedJson =
        """
          |{
          |  "owner":"fb93dda6-7e1f-439c-a249-0d4c22252858",
          |  "creationTime":1437090963326,
          |  "category":"testCategory",
          |  "subcategory":"testSubcategory",
          |  "data": {
          |    "testField1":"testValue1",
          |    "testField2":"testValue2"
          |  }
          |}
        """.stripMargin

      val dataItem = mock[DataItem]
      dataItem.ownerID returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")
      dataItem.creationTime returns new DateTime(1437090963326L)
      dataItem.category returns "testCategory"
      dataItem.subcategory returns "testSubcategory"
      dataItem.data returns Map("testField1" -> "testValue1", "testField2" -> "testValue2")

      new JsonDataItemWriter().toJson(dataItem) mustEqual Json.parse(expectedJson)
    }
  }

}
