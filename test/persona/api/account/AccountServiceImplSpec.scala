package persona.api.account

import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.runner.JUnitRunner
import persona.api.account.personal._
import persona.api.authentication.User
import play.api.test.PlaySpecification

import scala.concurrent.{ExecutionContext, Future}

@RunWith(classOf[JUnitRunner])
class AccountServiceImplSpec extends PlaySpecification with Mockito {

  "AccountServiceImpl" should {
    "retrieve valid information" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      val testDataItem = mock[DataItem]
      testDataItem.category returns "testCategory"
      testDataItem.subcategory returns "testSubCategory"

      val personalDataDao = mock[PersonalDataDAO]
      personalDataDao.listInformation(any[User])(any[ExecutionContext]) returns Future.successful(Seq(testDataItem))

      val dataItemValidator = mock[DataItemValidator]

      val accountService = new AccountServiceImpl(personalDataDao, dataItemValidator)
      val futureInformation = accountService.listInformation(mock[User])

      val information = await(futureInformation)
      information must have size 1
      information.head.category mustEqual testDataItem.category
    }

    "detect corrupted data items (i.e. schema has changed)" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      val testDataItem = mock[DataItem]
      testDataItem.category returns "testCategory"
      testDataItem.subcategory returns "testSubCategory"

      val personalDataDao = mock[PersonalDataDAO]
      personalDataDao.listInformation(any[User])(any[ExecutionContext]) returns Future.successful(Seq(testDataItem))

      val dataItemValidator = mock[DataItemValidator]
      dataItemValidator.ensureValid(any[DataItem]) throws new InvalidDataException("Corrupted data")

      val accountService = new AccountServiceImpl(personalDataDao, dataItemValidator)
      val futureInformation = accountService.listInformation(mock[User])

      await(futureInformation) must throwAn[InvalidDataException]
    }
  }

}
