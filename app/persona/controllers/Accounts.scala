package persona.controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import persona.api.account.AccountService
import persona.api.account.personal.JsonDataItemWriter
import persona.model.authentication.User
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

@Singleton
class Accounts @Inject() (
  accountService: AccountService,
  jsonDataItemWriter: JsonDataItemWriter) extends Controller {

  def listInformation = Action.async {
    val loginInfo = LoginInfo("facebook", "testUser")
    val test_uuid = UUID.fromString("da73919b-3650-4cc7-be06-b74ef16c4b3a")
    val test_user = new User(test_uuid, loginInfo)

    accountService.listInformation(test_user) map { dataItems =>
      val jsonDataItems = dataItems.map(jsonDataItemWriter.toJson)
      val serializedJson = Json.toJson(jsonDataItems)

      Ok(serializedJson)
    }
  }

}
