package com.persona.http.offer

import java.util.UUID

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
    pathPrefix("offer") {
      pathEndOrSingleSlash {
        get {
          onComplete(offerService.list()) {
            case Success(offers) => complete(StatusCodes.OK, offers.toJson)
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path(Segment) { seg =>
          val id = UUID.fromString(seg)

          pathEndOrSingleSlash {
            onComplete(offerService.get(id)) {
              case Success(maybeOffer) =>
                maybeOffer.map { offer =>
                  complete(StatusCodes.OK, offer.toJson)
                } getOrElse {
                  complete(StatusCodes.NotFound)
                }

              case Failure(e) => complete(StatusCodes.InternalServerError)
            }
          } ~
          pathPrefix("participate") {
            pathEndOrSingleSlash {
              complete(StatusCodes.InternalServerError, "TODO")
            }
          }
        }
      }
    }
  }
}
