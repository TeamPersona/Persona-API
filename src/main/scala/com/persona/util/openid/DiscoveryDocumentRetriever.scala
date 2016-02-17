package com.persona.util.openid

import akka.actor._
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.ImplicitMaterializer
import com.persona.util.http.HttpRetriever

class DiscoveryDocumentRetriever(recipient: ActorRef, http: HttpExt, discoveryUrl: String)
  extends Actor
    with ImplicitMaterializer
    with SprayJsonSupport
    with DiscoveryDocumentJsonProtocol {

  // Make a child actor that will send us the http response for discoveryUrl
  context.actorOf(Props(new HttpRetriever(self, http, discoveryUrl)))

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case response: HttpResponse if StatusCodes.OK == response.status =>
      Unmarshal(response.entity).to[DiscoveryDocument].map { document =>
        recipient ! document
      }
  }

}
