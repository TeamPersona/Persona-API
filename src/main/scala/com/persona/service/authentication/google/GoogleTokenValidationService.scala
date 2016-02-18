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

private object GoogleTokenValidationServiceActor {

  val DiscoveryDocumentUrl = "https://accounts.google.com/.well-known/openid-configuration"
  val Issuers = List("https://accounts.google.com", "accounts.google.com")

  case class Validate(idToken: JWT)

}

private class GoogleTokenValidationServiceActor(clientID: String, http: HttpExt) extends Actor with Stash {

  context.actorOf(Props(new DiscoveryDocumentJwkRetriever(self, http, GoogleTokenValidationServiceActor.DiscoveryDocumentUrl)))

  private[this] implicit val executionContext = context.dispatcher
  private[this] var validators = List.empty[IDTokenValidator]

  def receive: Receive = {
    case GoogleTokenValidationServiceActor.Validate(idToken) =>
      stash()

    case jwkSet: JWKSet =>
      update(jwkSet)
      unstashAll()
      context.become(initializedReceive)
  }

  def initializedReceive: Receive = {
    case GoogleTokenValidationServiceActor.Validate(idToken) =>
      val authenticated = validators.exists { validator =>
        Try(validator.validate(idToken, None.orNull)).isSuccess
      }

      sender ! authenticated

    case jwkSet: JWKSet =>
      update(jwkSet)
  }

  private[this] def update(jwkSet: JWKSet) = {
    validators = GoogleTokenValidationServiceActor.Issuers.map { issuer =>
      new IDTokenValidator(
        new Issuer(issuer),
        new ClientID(clientID),
        JWSAlgorithm.RS256,
        jwkSet
      )
    }
  }

}

object GoogleTokenValidationService {

  private val ValidateTimeout = Timeout(60.seconds)

  def apply(clientID: String, http: HttpExt)(implicit actorSystem: ActorSystem): GoogleTokenValidationService = {
    val actor = actorSystem.actorOf(
      Props(
        new GoogleTokenValidationServiceActor(clientID, http)
      )
    )

    new GoogleTokenValidationService(actor)
  }

}

class GoogleTokenValidationService private(actor: ActorRef) extends ActorWrapper(actor) {

  def validate(idToken: JWT)(implicit executionContext: ExecutionContext): Future[Boolean] = {
    implicit val timeout = GoogleTokenValidationService.ValidateTimeout
    val futureResult = actor ? GoogleTokenValidationServiceActor.Validate(idToken)

    futureResult.map { result =>
      result.asInstanceOf[Boolean]
    }
  }

}
