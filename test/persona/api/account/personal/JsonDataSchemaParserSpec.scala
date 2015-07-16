package persona.api.account.personal

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import persona.util.ParseException

@RunWith(classOf[JUnitRunner])
class JsonDataSchemaParserSpec extends Specification {

  "JsonDataSchemaParser" should {
    "parse json with one field" in {
      val json =
        """
          |{
          |  "category":"test",
          |  "subcategory":"subtest",
          |  "fields":[
          |    {
          |      "name":"field1",
          |      "isRequired":false,
          |      "type":"int"
          |    }]
          |}
        """.stripMargin

      val tryDataSchema = new JsonDataSchemaParser().parse(json)
      tryDataSchema.isSuccess must beTrue

      val dataSchema = tryDataSchema.get
      dataSchema.category mustEqual "test"
      dataSchema.subcategory mustEqual "subtest"
    }

    "parse json with multiple fields" in {
      val json =
        """
          |{
          |  "category":"test",
          |  "subcategory":"subtest",
          |  "fields":[
          |    {
          |      "name":"field1",
          |      "isRequired":false,
          |      "type":"int"
          |    },
          |    {
          |      "name":"field2",
          |      "isRequired":true,
          |      "type":"string"
          |    }]
          |}
        """.stripMargin

      val tryDataSchema = new JsonDataSchemaParser().parse(json)
      tryDataSchema.isSuccess must beTrue

      val dataSchema = tryDataSchema.get
      dataSchema.category mustEqual "test"
      dataSchema.subcategory mustEqual "subtest"
    }
  }

  "throw exception for invalid json" in {
    val json = "{"

    val tryDataSchema = new JsonDataSchemaParser().parse(json)
    tryDataSchema.isFailure must beTrue
    tryDataSchema.get must throwAn[Exception]
  }

  "throw exception for json missing required field" in {
    val json =
      """
        |{
        |  "category":"test"
        |}
      """.stripMargin

    val tryDataSchema = new JsonDataSchemaParser().parse(json)
    tryDataSchema.isFailure must beTrue
    tryDataSchema.get must throwA[ParseException]
  }

}