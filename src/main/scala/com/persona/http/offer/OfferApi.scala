package com.persona.http.offer


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import com.persona.service.offer.{OfferJsonProtocol, OfferService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import spray.json._

class OfferApi(offerService: OfferService)(implicit ec: ExecutionContext)
  extends SprayJsonSupport
    with OfferJsonProtocol {

  val route = {
    redirectToNoTrailingSlashIfPresent(StatusCodes.Found) {
      pathPrefix("offer") {
        pathPrefix("list") {
          path(IntNumber) { id =>
            onComplete(offerService.list(id)) {
              case Success(offers) => complete(StatusCodes.OK, offers.toJson)
              case Failure(e) => complete(StatusCodes.InternalServerError)
            }
          }
        } ~
        pathPrefix("participate") {
          //complete(StatusCodes.InternalServerError, "TODO")
          complete ("TODO")
        } ~
        path(IntNumber) { id =>
          onComplete(offerService.get(id)) {
            case Success(maybeOffer) =>
              maybeOffer.map { offer =>
                complete(StatusCodes.OK, offer.toJson)
              } getOrElse {
                complete(StatusCodes.NotFound)
              }
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}

