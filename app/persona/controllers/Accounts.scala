package persona.controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import persona.api.account.AccountService
import persona.model.authentication.User
import persona.api.account.personal.{JsonDataItemParser, JsonDataItemWriter}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

@Singleton
class Accounts @Inject() (
  accountService: AccountService,
  jsonDataItemWriter: JsonDataItemWriter,
  jsonDataItemParser: JsonDataItemParser) extends Controller with PersonaControllerHelper {

  def listInformation = Action.async {
    val loginInfo = LoginInfo("facebook", "testUser")
    val test_uuid = UUID.fromString("da73919b-3650-4cc7-be06-b74ef16c4b3a")
    val test_user = new User(test_uuid, loginInfo)

    accountService.listInformation(test_user) map { dataItems =>
      val json = jsonDataItemWriter.toJson(dataItems)

      Ok(json)
    }
  }

  def saveInformation = Action.async(parse.json) { request =>
    val loginInfo = LoginInfo("facebook", "testUser")
    val test_uuid = UUID.fromString("da73919b-3650-4cc7-be06-b74ef16c4b3a")
    val test_user = new User(test_uuid, loginInfo)
    val parseResult = jsonDataItemParser.parse(test_user, request.body)

    // Check for missing json fields
    parseResult.fold(parseErrors => {
      val json = generateErrorJson(parseErrors)

      Future.successful(BadRequest(json))
    }, dataItem => {
      val validationResult = accountService.saveInformation(dataItem)

      // Check if the data we were given is valid
      validationResult.fold(validationErrors => {
        val json = generateErrorJson(validationErrors)

        Future.successful(BadRequest(json))
      }, futureSaveResult => {
        // We got valid data!  Wait for the database to finish the insert
        futureSaveResult map { saveResult =>
          val json = generateSuccessJson

          Ok(json)
        }
      })
    })
  }

}
