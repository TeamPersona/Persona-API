package com.persona.util.jwk

import akka.actor._
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.ImplicitMaterializer
import com.nimbusds.jose.jwk.JWKSet
import com.persona.util.http.HttpRetriever

class HttpJwkRetriever(recipient: ActorRef, http: HttpExt, jwkUrl: String)
  extends Actor
    with ImplicitMaterializer
    with SprayJsonSupport
    with JwkJsonProtocol {

  // Make a child actor that will send us the http response for jwkUrl
  context.actorOf(Props(new HttpRetriever(self, http, jwkUrl)))

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case response: HttpResponse if StatusCodes.OK == response.status =>
      Unmarshal(response.entity).to[JWKSet].map { jwkSet =>
        recipient ! jwkSet
      }
  }

}
