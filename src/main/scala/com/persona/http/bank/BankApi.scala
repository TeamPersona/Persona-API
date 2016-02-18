package com.persona.http.bank

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.http.JsonPersonaError
import com.persona.service.account.Account
import com.persona.service.bank.{BankService, DataItemJsonProtocol, RawDataItem}
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.Success

class BankApi(bankService: BankService)(implicit ec: ExecutionContext)
  extends SprayJsonSupport
    with DataItemJsonProtocol
    with JsonPersonaError {

  val route = {
    pathPrefix("bank") {
      pathEndOrSingleSlash {
        post {
          entity(as[RawDataItem]) { rawDataItem =>
            val dataItem = rawDataItem.process()
            val testAccount = new Account(0, "john", "smith", "johnsmith@example.com", "123-456-7890")

            onComplete(bankService.saveInformation(testAccount, dataItem)) {
              case Success(result) =>
                result.fold(parseErrors => {
                  complete(StatusCodes.BadRequest, errorJson(parseErrors))
                }, _ => {
                  complete(StatusCodes.OK)
                })

              case _ => complete(StatusCodes.InternalServerError)
            }
          }
        } ~
        get {
          val testAccount = new Account(0, "john", "smith", "johnsmith@example.com", "123-456-7890")

          onComplete(bankService.listInformation(testAccount)) {
            case Success(dataItems) => complete(dataItems.toJson)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}
