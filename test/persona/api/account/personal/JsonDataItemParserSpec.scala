package persona.api.account.personal

import java.util.UUID

import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.api.authentication.User
import persona.util.ValidationError
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class JsonDataItemParserSpec extends Specification with Mockito {

  "JsonDataItemParserSpec" should {
    "parse valid data item" in {
      val json =
        """
          |{
          |  "category":"testCategory",
          |  "subcategory":"testSubcategory",
          |  "data": {
          |    "field":"value"
          |  }
          |}
        """.stripMargin

      val mockUser = mock[User]
      mockUser.id returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")

      val maybeDataItem = new JsonDataItemParser().parse(mockUser, Json.parse(json))
      maybeDataItem.isSuccess must beTrue

      val dataItem = maybeDataItem.toOption.get
      dataItem.category mustEqual "testCategory"
      dataItem.subcategory mustEqual "testSubcategory"
    }

    "fail to parse json missing fields" in {
      val json =
        """
          |{
          |  "category":"testCategory"
          |}
        """.stripMargin

      val mockUser = mock[User]
      mockUser.id returns UUID.fromString("fb93dda6-7e1f-439c-a249-0d4c22252858")

      val maybeDataItem = new JsonDataItemParser().parse(mockUser, Json.parse(json))

      maybeDataItem.disjunction.leftMap { parseErrors =>
        parseErrors.size mustEqual 1
        parseErrors.head must beAnInstanceOf[ValidationError]
      }

      maybeDataItem.isSuccess must beFalse
    }
  }

}
