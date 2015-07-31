package persona.controllers

import com.websudos.phantom.dsl.ResultSet
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mock.Mockito
import org.specs2.runner._
import persona.api.account.AccountService
import persona.api.account.personal.{DataItem, DataItemValidationError, JsonDataItemParser, JsonDataItemWriter}
import persona.model.authentication.User
import persona.util.ParseError
import play.api.http.MimeTypes
import play.api.libs.iteratee.Input
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.Scalaz._

@RunWith(classOf[JUnitRunner])
class AccountsSpec extends PlaySpecification with Mockito {

  "Accounts.listInformation" should {
    "OK request when there is no information available" in {
      val mockDataItemJson = Json.parse("[]")

      val accountService = mock[AccountService]
      accountService.listInformation(any[User])(any[ExecutionContext]) returns Future.successful(Seq())

      val jsonDataItemWriter = mock[JsonDataItemWriter]
      jsonDataItemWriter.toJson(any[Seq[DataItem]]) returns mockDataItemJson

      val accountsController = new Accounts(accountService, jsonDataItemWriter, mock[JsonDataItemParser])
      val futureResult = accountsController.listInformation.apply(FakeRequest(GET, "/api/account/information"))

      status(futureResult) mustEqual OK
      contentType(futureResult).get mustEqual MimeTypes.JSON
      contentAsJson(futureResult) mustEqual mockDataItemJson
    }

    "OK request when there is information available" in {
      val mockDataItem = mock[DataItem]
      mockDataItem.creationTime returns new DateTime(1437090963326L)
      mockDataItem.category returns "testCategory"
      mockDataItem.subcategory returns "testSubcategory"
      mockDataItem.data returns Map("testField" -> "testValue")

      val mockDataItemJson = Json.parse(
        """
          |{
          |  "creationTime":1437090963326,
          |  "category":"testCategory",
          |  "subcategory":"testSubcategory",
          |  "data": {
          |    "testField":"testValue"
          |  }
          |}
        """.stripMargin)

      val accountService = mock[AccountService]
      accountService.listInformation(any[User])(any[ExecutionContext]) returns Future.successful(Seq(mockDataItem))

      val jsonDataItemWriter = mock[JsonDataItemWriter]
      jsonDataItemWriter.toJson(any[Seq[DataItem]]) returns mockDataItemJson

      val accountsController = new Accounts(accountService, jsonDataItemWriter, mock[JsonDataItemParser])
      val futureResult = accountsController.listInformation.apply(FakeRequest(GET, "/api/account/information"))

      status(futureResult) mustEqual OK
      contentType(futureResult).get mustEqual MimeTypes.JSON
      contentAsJson(futureResult) mustEqual mockDataItemJson
    }
  }

  "Accounts.saveInformation" should {
    "BadRequest when there are missing JSON fields" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      val parseError = mock[ParseError]
      parseError.errorMessage returns "Test parse error"

      val jsonDataItemParser = mock[JsonDataItemParser]
      jsonDataItemParser.parse(any[User], any[JsValue]) returns parseError.failureNel

      val json = "{}"

      val request = FakeRequest(POST, "/api/account/information")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.parse(json))

      val accountsController = new Accounts(mock[AccountService], mock[JsonDataItemWriter], jsonDataItemParser)

      // See: https://revoltingcode.wordpress.com/2013/10/27/play-framework-2-controller-testing-with-json-body-parser
      val futureResult = accountsController.saveInformation.apply(request).feed(Input.El(json.getBytes)).flatMap(_.run)

      status(futureResult) mustEqual BAD_REQUEST
      contentType(futureResult).get mustEqual MimeTypes.JSON
    }

    "BadRequest when there are invalid fields" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      val jsonDataItemParser = mock[JsonDataItemParser]
      jsonDataItemParser.parse(any[User], any[JsValue]) returns mock[DataItem].successNel

      val dataItemValidationError = mock[DataItemValidationError]
      dataItemValidationError.errorMessage returns "Test validation error"

      val accountService = mock[AccountService]
      accountService.saveInformation(any[DataItem])(any[ExecutionContext]) returns dataItemValidationError.failureNel

      val json = "{}"

      val request = FakeRequest(POST, "/api/account/information")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.parse(json))

      val accountsController = new Accounts(accountService, mock[JsonDataItemWriter], jsonDataItemParser)

      // See: https://revoltingcode.wordpress.com/2013/10/27/play-framework-2-controller-testing-with-json-body-parser
      val futureResult = accountsController.saveInformation.apply(request).feed(Input.El(json.getBytes)).flatMap(_.run)

      status(futureResult) mustEqual BAD_REQUEST
      contentType(futureResult).get mustEqual MimeTypes.JSON
    }

    "OK request when given valid data" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      val jsonDataItemParser = mock[JsonDataItemParser]
      jsonDataItemParser.parse(any[User], any[JsValue]) returns mock[DataItem].successNel

      val accountService = mock[AccountService]
      accountService.saveInformation(any[DataItem])(any[ExecutionContext]) returns Future.successful(mock[ResultSet]).successNel

      val json = "{}"

      val request = FakeRequest(POST, "/api/account/information")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.parse(json))

      val accountsController = new Accounts(accountService, mock[JsonDataItemWriter], jsonDataItemParser)

      // See: https://revoltingcode.wordpress.com/2013/10/27/play-framework-2-controller-testing-with-json-body-parser
      val futureResult = accountsController.saveInformation.apply(request).feed(Input.El(json.getBytes)).flatMap(_.run)

      status(futureResult) mustEqual OK
      contentType(futureResult).get mustEqual MimeTypes.JSON
    }
  }

}
