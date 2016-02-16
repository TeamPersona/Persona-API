package com.persona.service.authentication.google

import akka.actor._
import akka.http.scaladsl.HttpExt
import akka.pattern.ask
import akka.util.Timeout
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jwt.JWT
import com.nimbusds.oauth2.sdk.id.{ClientID, Issuer}
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator
import com.persona.util.actor.ActorWrapper
import com.persona.util.jwk.DiscoveryDocumentJwkRetriever

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

private object GoogleAuthServiceActor {

  case class Authenticate(idToken: JWT)

}

private class GoogleAuthServiceActor(clientID: String, http: HttpExt, discoveryUrl: String)
  extends Actor
    with Stash {

  context.actorOf(Props(new DiscoveryDocumentJwkRetriever(self, http, discoveryUrl)))

  private[this] implicit val executionContext = context.dispatcher
  private[this] var validators = List.empty[IDTokenValidator]

  def receive: Receive = {
    case GoogleAuthServiceActor.Authenticate(idToken) =>
      stash()

    case jwkSet: JWKSet =>
      update(jwkSet)
      unstashAll()
      context.become(initializedReceive)
  }

  def initializedReceive: Receive = {
    case GoogleAuthServiceActor.Authenticate(idToken) =>
      val authenticated = validators.exists { validator =>
        Try(validator.validate(idToken, null)).isSuccess
      }

      sender ! authenticated

    case jwkSet: JWKSet =>
      update(jwkSet)
  }

  private[this] def update(jwkSet: JWKSet) = {
    validators = GoogleAuthService.Issuers.map { issuer =>
      new IDTokenValidator(
        new Issuer(issuer),
        new ClientID(clientID),
        JWSAlgorithm.RS256,
        jwkSet
      )
    }
  }

}

object GoogleAuthService {

  private val AuthenticateTimeout = Timeout(60.seconds)

  val DiscoveryDocumentUrl = "https://accounts.google.com/.well-known/openid-configuration"
  val Issuers = List("https://accounts.google.com", "accounts.google.com")

  def apply(clientID: String, http: HttpExt)(implicit actorSystem: ActorSystem): GoogleAuthService = {
    val actor = actorSystem.actorOf(
      Props(
        new GoogleAuthServiceActor(clientID, http, GoogleAuthService.DiscoveryDocumentUrl)
      )
    )

    new GoogleAuthService(actor)
  }

}

class GoogleAuthService private(actor: ActorRef) extends ActorWrapper(actor) {

  def authenticate(idToken: JWT)(implicit executionContext: ExecutionContext): Future[Boolean] = {
    implicit val timeout = GoogleAuthService.AuthenticateTimeout
    val futureResult = actor ? GoogleAuthServiceActor.Authenticate(idToken)

    futureResult.map { result =>
      result.asInstanceOf[Boolean]
    }
  }

}
