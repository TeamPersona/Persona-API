package com.persona.http.bank

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import com.persona.http.{JsonPersonaError, PersonaOAuth2Utils}
import com.persona.service.authorization.AuthorizationService
import com.persona.service.bank.{BankService, DataItemJsonProtocol, RawDataItem}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import spray.json._

class BankApi
  (
    bankService: BankService,
    authorizationService: AuthorizationService
  )
  (
    implicit ec: ExecutionContext
  )
  extends SprayJsonSupport
    with DataItemJsonProtocol
    with JsonPersonaError
    with PersonaOAuth2Utils {

  val route = {
    pathPrefix("bank") {
      pathEndOrSingleSlash {
        post {
          entity(as[RawDataItem]) { rawDataItem =>
            oauth2Token { token =>
              onComplete(authorizationService.validate(token)) {
                case Success(Some((account, _))) =>
                  val dataItem = rawDataItem.process()

                  onComplete(bankService.insert(account, dataItem)) {
                    case Success(result) =>
                      result.fold(parseErrors => {
                        complete(StatusCodes.BadRequest, errorJson(parseErrors))
                      }, _ => {
                        complete(StatusCodes.OK)
                      })

                    case _ => complete(StatusCodes.InternalServerError)
                  }

                case Success(None) =>
                  complete(StatusCodes.BadRequest)

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        } ~
        get {
          oauth2Token { token =>
            onComplete(authorizationService.validate(token)) {
              case Success(Some((account, _))) =>
                onComplete(bankService.retrieve(account)) {
                  case Success(dataItems) =>
                    complete(dataItems.toJson)

                  case Failure(e) =>
                    complete(StatusCodes.InternalServerError)
                }

              case Success(None) =>
                complete(StatusCodes.BadRequest)

              case Failure(e) =>
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }
}
