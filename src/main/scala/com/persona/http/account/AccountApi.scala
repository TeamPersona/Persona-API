package com.persona.http.account

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import com.nimbusds.jwt.SignedJWT

import com.persona.http.{PersonaOAuth2Utils, JsonPersonaError}
import com.persona.service.account.google.GoogleAccountService
import com.persona.service.account.{AccountJsonProtocol, AccountDescriptor, AccountService, AccountValidator}
import com.persona.service.authorization.{AuthorizationResultJsonProtocol, AuthorizationService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

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
    with AccountJsonProtocol
    with AuthorizationResultJsonProtocol
    with JsonPersonaError
    with PersonaOAuth2Utils {

  val route = {
    pathPrefix("account") {
      pathEndOrSingleSlash {
        get {
          oauth2Token { token =>
            onComplete(authorizationService.validate(token)) {
              case Success(Some((account, _))) =>
                complete(account)

              case Success(None) =>
                complete(StatusCodes.BadRequest)

              case Failure(e) =>
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~
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
            formFields('id_token, 'phone_number, 'client_id) { (jwt, phoneNumber, clientId) =>
              Try(SignedJWT.parse(jwt)) match {
                case Success(idToken) =>
                  onComplete(accountService.retrieveThirdPartyAccount(clientId)) {
                    case Success(thirdPartyAccountOption) =>
                      thirdPartyAccountOption.map { thirdPartyAccount =>
                        onComplete(googleAccountService.create(idToken, phoneNumber)) {
                          case Success(creationResult) =>
                            creationResult.fold({ errors =>
                              complete(StatusCodes.BadRequest, errorJson(errors))
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
