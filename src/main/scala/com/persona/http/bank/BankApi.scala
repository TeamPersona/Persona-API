package com.persona.http.bank

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.service.account.Account
import com.persona.service.bank.{BankService, DataItemJsonProtocol, RawDataItem}
import com.persona.util.PersonaError
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.Success
import scalaz.NonEmptyList

class BankApi(bankService: BankService)(implicit ec: ExecutionContext)
  extends SprayJsonSupport
    with DataItemJsonProtocol {

  val route = {
    pathPrefix("bank") {
      pathEndOrSingleSlash {
        post {
          entity(as[RawDataItem]) { rawDataItem =>
            val dataItem = rawDataItem.process()
            val testId = UUID.fromString("da73919b-3650-4cc7-be06-b74ef16c4b3a")
            val testAccount = new Account(testId)

            onComplete(bankService.saveInformation(testAccount, dataItem)) {
              case Success(result) =>
                result.fold(parseErrors => {
                  complete(StatusCodes.BadRequest, generateErrorJson(parseErrors))
                }, _ => {
                  complete(StatusCodes.OK)
                })

              case _ => complete(StatusCodes.InternalServerError)
            }
          }
        } ~
        get {
          val testId = UUID.fromString("da73919b-3650-4cc7-be06-b74ef16c4b3a")
          val testAccount = new Account(testId)

          onComplete(bankService.listInformation(testAccount)) {
            case Success(dataItems) => complete(dataItems.toJson)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }

  def generateErrorJson[T <: PersonaError](errors: NonEmptyList[T]): JsValue = {
    val errorMessages = errors.list.map { error =>
      JsString(error.errorMessage)
    }

    JsArray(errorMessages.toVector)
  }
}
