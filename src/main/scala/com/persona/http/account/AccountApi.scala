package com.persona.http.account

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import com.nimbusds.jwt.SignedJWT

import com.persona.http.JsonPersonaError
import com.persona.service.account.google.GoogleAccountService
import com.persona.service.account.{AccountValidator, AccountDescriptor, AccountService}
import com.persona.service.authorization.{AuthorizationResultJsonProtocol, AuthorizationService}

import scala.concurrent.ExecutionContext
import scala.util.{Try, Failure, Success}

import spray.json._

class AccountApi
  (
    accountService: AccountService,
    accountValidator: AccountValidator,
    googleAccountService: GoogleAccountService,
    authorizationService: AuthorizationService
  )
  (
    implicit ec: ExecutionContext
  )
  extends SprayJsonSupport
    with JsonPersonaError
    with AuthorizationResultJsonProtocol {

  val route = {
    pathPrefix("account") {
      pathEndOrSingleSlash {
        post {
          formFields('given_name, 'family_name, 'email, 'phone_number).as(AccountDescriptor) { accountDescriptor =>
            accountValidator.validate(accountDescriptor).fold({ errors =>
              complete(StatusCodes.BadRequest, errorJson(errors))
            }, { _ =>
              formField('password, 'client_id) { (password, clientId) =>
                onComplete(accountService.retrieveThirdPartyAccount(clientId)) {
                  case Success(thirdPartyAccountOption) =>
                    thirdPartyAccountOption.map { thirdPartyAccount =>
                      onComplete(accountService.create(accountDescriptor, password)) {
                        case Success(validationResult) =>
                          validationResult.fold({ _ =>
                            complete(StatusCodes.Conflict)
                          }, { account =>
                            onComplete(authorizationService.authorize(account, thirdPartyAccount)) {
                              case Success(result) =>
                                complete(result)

                              case Failure(e) =>
                                complete(StatusCodes.InternalServerError)
                            }
                          })

                        case Failure(e) =>
                          complete(StatusCodes.InternalServerError)
                      }
                    } getOrElse {
                      complete(StatusCodes.BadRequest)
                    }

                  case Failure(e) =>
                    complete(StatusCodes.InternalServerError)
                }
              }
            })
          }
        }
      } ~
      path("google") {
        pathEndOrSingleSlash {
          post {
            formField("id_token") { jwt =>
              Try(SignedJWT.parse(jwt)) match {
                case Success(idToken) =>
                  formField('phone_number) { phoneNumber =>
                    onComplete(googleAccountService.create(idToken, phoneNumber)) {
                      case Success(creationResult) =>
                        creationResult.fold({ errors =>
                          complete(StatusCodes.BadRequest, errorJson(errors))
                        }, { _ =>
                          complete("""{ "code": "abcdefghikl" }""".parseJson)
                        })

                      case Failure(e) =>
                        complete(StatusCodes.InternalServerError)
                    }
                  }

                case Failure(e) =>
                  complete(StatusCodes.BadRequest)
              }
            }
          }
        }
      }
    }
  }

}
