package persona.api.account.personal

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

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

      val maybeDataSchema = new JsonDataSchemaParser().parse(json)
      maybeDataSchema.isSuccess must beTrue

      val dataSchema = maybeDataSchema.toOption.get
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

      val maybeDataSchema = new JsonDataSchemaParser().parse(json)
      maybeDataSchema.isSuccess must beTrue

      val dataSchema = maybeDataSchema.toOption.get
      dataSchema.category mustEqual "test"
      dataSchema.subcategory mustEqual "subtest"
    }
  }

  "fail to parse invalid json" in {
    val json = "{"

    val maybeDataSchema = new JsonDataSchemaParser().parse(json)

    maybeDataSchema.disjunction.leftMap { dataSchemaParseErrors =>
      dataSchemaParseErrors.size mustEqual 1
      dataSchemaParseErrors.head must beAnInstanceOf[BadFormatError]
    }

    maybeDataSchema.isSuccess must beFalse
  }

  "fail to parse json missing required fields" in {
    val json =
      """
        |{
        |  "category":"test"
        |}
      """.stripMargin

    val maybeDataSchema = new JsonDataSchemaParser().parse(json)

    maybeDataSchema.disjunction.leftMap { dataSchemaParseErrors =>
      dataSchemaParseErrors.size mustEqual 1
      dataSchemaParseErrors.head must beAnInstanceOf[ValidationError]
    }

    maybeDataSchema.isSuccess must beFalse
  }

}