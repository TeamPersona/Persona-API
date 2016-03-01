package com.persona.service.account.google

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import com.nimbusds.jwt.JWT
import com.persona.service.account.Account

import com.persona.service.authentication.google.GoogleTokenValidationService
import com.persona.util.actor.ActorWrapper

import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

import scalaz.Scalaz._
import scalaz.ValidationNel

object GoogleAccountServiceActor {

  case class Create(idToken: JWT, phoneNumber: String)

}

class GoogleAccountServiceActor(
  converter: GoogleTokenConverter,
  googleAccountDAO: GoogleAccountDAO,
  validationService: GoogleTokenValidationService) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive = {
    case GoogleAccountServiceActor.Create(idToken, phoneNumber) =>
      validateToken(idToken, phoneNumber, sender)
  }

  private[this] def validateToken(idToken: JWT, phoneNumber: String, actor: ActorRef) = {
    validationService.validate(idToken).onComplete {
      case Success(valid) =>
        if(valid) {
          convertToken(idToken, phoneNumber, actor)
        } else {
          actor ! (new InvalidIdTokenError).failureNel
        }

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

  private[this] def convertToken(idToken: JWT, phoneNumber: String, actor: ActorRef) = {
    converter.convert(idToken, phoneNumber).fold({ errors =>
      actor ! errors.failure
    }, { googleAccountDescriptor =>
      validateUniqueAccount(googleAccountDescriptor, actor)
    })
  }

  private[this] def validateUniqueAccount(googleAccountDescriptor: GoogleAccountDescriptor, actor: ActorRef) = {
    googleAccountDAO.exists(googleAccountDescriptor).onComplete {
      case Success(exists) =>
        if(exists) {
          actor ! (new GoogleAccountAlreadyExistsError).failureNel
        } else {
          createAccount(googleAccountDescriptor, actor)
        }

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

  private[this] def createAccount(googleAccountDescriptor: GoogleAccountDescriptor, actor: ActorRef) = {
    googleAccountDAO.create(googleAccountDescriptor).onComplete {
      case Success(result) =>
        actor ! result.successNel

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }
}

object GoogleAccountService {

  private val createTimeout = Timeout(60.seconds)

  def apply(converter: GoogleTokenConverter, googleAccountDAO: GoogleAccountDAO, validationService: GoogleTokenValidationService)
           (implicit actorSystem: ActorSystem): GoogleAccountService = {
    val actor = actorSystem.actorOf(
      Props(
        new GoogleAccountServiceActor(converter, googleAccountDAO, validationService)
      )
    )

    new GoogleAccountService(actor)
  }

}

class GoogleAccountService private(actor: ActorRef) extends ActorWrapper(actor) {

  def create(idToken: JWT, phoneNumber: String)
            (implicit ec: ExecutionContext): Future[ValidationNel[GoogleAccountValidationError, Account]] = {
    implicit val timeout = GoogleAccountService.createTimeout
    val futureResult = actor ? GoogleAccountServiceActor.Create(idToken, phoneNumber)

    futureResult.map { result =>
      result.asInstanceOf[ValidationNel[GoogleAccountValidationError, Account]]
    }
  }

}
