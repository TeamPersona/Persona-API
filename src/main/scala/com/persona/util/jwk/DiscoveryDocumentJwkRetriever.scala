package com.persona.util.jwk

import akka.actor._
import akka.http.scaladsl.HttpExt
import com.persona.util.openid.{DiscoveryDocument, DiscoveryDocumentRetriever}

class DiscoveryDocumentJwkRetriever(recipient: ActorRef, http: HttpExt, discoveryUrl: String)
  extends Actor {

  context.actorOf(Props(new DiscoveryDocumentRetriever(self, http, discoveryUrl)))

  private[this] implicit val executionContext = context.dispatcher
  private[this] var httpJwkRetrieverOption: Option[ActorRef] = None

  def receive: Receive = {
    case document: DiscoveryDocument =>
      httpJwkRetrieverOption.foreach { httpJwkRetriever =>
        httpJwkRetriever ! PoisonPill
      }

      httpJwkRetrieverOption = Some(
        context.actorOf(
          Props(
            new HttpJwkRetriever(recipient, http, document.jwksUri)
          )
        )
      )
  }

}
