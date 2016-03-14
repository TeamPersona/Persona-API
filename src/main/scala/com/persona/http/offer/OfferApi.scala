package com.persona.http.offer


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.persona.http.PersonaOAuth2Utils
import com.persona.service.authorization.AuthorizationService

import com.persona.service.offer.{OfferJsonProtocol, OfferService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import spray.json._

class OfferApi(offerService: OfferService, authorizationService: AuthorizationService)(implicit ec: ExecutionContext)
  extends SprayJsonSupport
    with OfferJsonProtocol
    with PersonaOAuth2Utils {

  val route = {
    redirectToNoTrailingSlashIfPresent(StatusCodes.Found) {
      pathPrefix("offer") {
        pathPrefix("list") {
          path(IntNumber) { offerid =>
            oauth2Token { token =>
              onComplete(authorizationService.validate(token)) {
                case Success(Some((account, _))) =>
                  onComplete(offerService.list(account, offerid)) {
                    case Success(offers) => complete(StatusCodes.OK, offers.toJson)
                    case Failure(e) => complete(StatusCodes.InternalServerError)
                  }

                case Success(None) =>
                  complete(StatusCodes.BadRequest)

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        } ~
        pathPrefix("participate") {
          path(IntNumber) { offerid =>
            oauth2Token { token =>
              onComplete(authorizationService.validate(token)) {
                case Success(Some((account, _))) =>
                  onComplete(offerService.participate(account, offerid)) {
                    case Success(successful) => complete(StatusCodes.OK, successful.toJson)
                    case Failure(e) => complete(StatusCodes.InternalServerError)
                  }
                case Success(None) =>
                  complete(StatusCodes.BadRequest)

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        } ~
          pathPrefix("unparticipate") {
            path(IntNumber) { offerid =>
              oauth2Token { token =>
                onComplete(authorizationService.validate(token)) {
                  case Success(Some((account, _))) =>
                    onComplete(offerService.unparticipate(account, offerid)) {
                      case Success(successful) => complete(StatusCodes.OK, successful.toJson)
                      case Failure(e) => complete(StatusCodes.InternalServerError)
                    }
                  case Success(None) =>
                    complete(StatusCodes.BadRequest)

                  case Failure(e) =>
                    complete(StatusCodes.InternalServerError)
                }
              }
            }
          } ~
          pathPrefix("recommended") {
              oauth2Token { token =>
                onComplete(authorizationService.validate(token)) {
                  case Success(Some((account, _))) =>
                    onComplete(offerService.getRecommended(account)) {
                      case Success(successful) => complete(StatusCodes.OK, successful.toJson)
                      case Failure(e) => complete(StatusCodes.InternalServerError)
                    }
                  case Success(None) =>
                    complete(StatusCodes.BadRequest)

                  case Failure(e) =>
                    complete(StatusCodes.InternalServerError)
                }
              }
          } ~
          pathPrefix("pending") {
            oauth2Token { token =>
              onComplete(authorizationService.validate(token)) {
                case Success(Some((account, _))) =>
                  onComplete(offerService.getPending(account)) {
                    case Success(successful) => complete(StatusCodes.OK, successful.toJson)
                    case Failure(e) => complete(StatusCodes.InternalServerError)
                  }
                case Success(None) =>
                  complete(StatusCodes.BadRequest)

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)
              }
            }
          } ~
          pathPrefix("completed") {
            oauth2Token { token =>
              onComplete(authorizationService.validate(token)) {
                case Success(Some((account, _))) =>
                  onComplete(offerService.getCompleted(account)) {
                    case Success(successful) => complete(StatusCodes.OK, successful.toJson)
                    case Failure(e) => complete(StatusCodes.InternalServerError)
                  }
                case Success(None) =>
                  complete(StatusCodes.BadRequest)

                case Failure(e) =>
                  complete(StatusCodes.InternalServerError)
              }
            }
          } ~
        path(IntNumber) { id =>
          oauth2Token { token =>
            onComplete(authorizationService.validate(token)) {
              case Success(Some((account, _))) =>
                onComplete(offerService.get(account, id)) {
                  case Success(maybeOffer) =>
                    maybeOffer.map { offer =>
                      complete(StatusCodes.OK, offer.toJson)
                    } getOrElse {
                      complete(StatusCodes.NotFound)
                    }
                  case Failure(e) => complete(StatusCodes.InternalServerError)
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

