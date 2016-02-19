package com.persona.service.authentication.google

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import com.nimbusds.jwt.JWT

import com.persona.service.account.google.{GoogleTokenConverter, GoogleAccountDAO}
import com.persona.util.actor.ActorWrapper

import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

private object GoogleAuthenticationServiceActor {

  private class IdTokenMissingSubjectException extends RuntimeException

  case class Authenticate(idToken: JWT)

}

private class GoogleAuthenticationServiceActor(
  converter: GoogleTokenConverter,
  googleAccountDAO: GoogleAccountDAO,
  validationService: GoogleTokenValidationService) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case GoogleAuthenticationServiceActor.Authenticate(idToken) =>
      validateToken(idToken, sender)
  }

  private[this] def validateToken(idToken: JWT, actor: ActorRef) = {
    validationService.validate(idToken).onComplete {
      case Success(valid) =>
        if(valid) {
          convertToken(idToken, actor)
        } else {
          actor ! InvalidTokenAndAccountResult()
        }

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

  private[this] def convertToken(idToken: JWT, actor: ActorRef) = {
    converter.convert(idToken).fold({ _ =>
      // The subject field of a JWT should always be present
      throw new GoogleAuthenticationServiceActor.IdTokenMissingSubjectException
    }, { googleId =>
      validateAccountExists(googleId, actor)
    })
  }

  private[this] def validateAccountExists(googleId: String, actor: ActorRef) = {
    googleAccountDAO.exists(googleId).onComplete {
      case Success(exists) =>
        if(exists) {
          actor ! ValidTokenAndAccountResult()
        } else {
          actor ! ValidTokenInvalidAccountResult()
        }

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

}

object GoogleAuthenticationService {

  private val authenticateTimeout = Timeout(60.seconds)

  def apply(converter: GoogleTokenConverter, googleAccountDAO: GoogleAccountDAO, validationService: GoogleTokenValidationService)
           (implicit actorSystem: ActorSystem): GoogleAuthenticationService = {
    val actor = actorSystem.actorOf(
      Props(
        new GoogleAuthenticationServiceActor(converter, googleAccountDAO, validationService)
      )
    )

    new GoogleAuthenticationService(actor)
  }

}

class GoogleAuthenticationService private(actor: ActorRef) extends ActorWrapper(actor) {

  def authenticate(idToken: JWT)(implicit ec: ExecutionContext): Future[GoogleAuthenticationResult] = {
    implicit val timeout = GoogleAuthenticationService.authenticateTimeout
    val futureResult = actor ? GoogleAuthenticationServiceActor.Authenticate(idToken)

    futureResult.map { result =>
      result.asInstanceOf[GoogleAuthenticationResult]
    }
  }

}
