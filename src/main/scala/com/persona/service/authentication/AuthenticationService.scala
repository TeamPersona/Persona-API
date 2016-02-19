package com.persona.service.authentication

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import com.persona.service.account.AccountDAO
import com.persona.util.actor.ActorWrapper

import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object AuthenticationServiceActor {

  case class PasswordAuthenticate(email: String, password: String)

}

class AuthenticationServiceActor(accountDAO: AccountDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case AuthenticationServiceActor.PasswordAuthenticate(email, password) =>
      val actor = sender

      accountDAO.retrievePassword(email).onComplete {
        case Success(expectedPasswordOption) =>
          expectedPasswordOption match {
            case Some(expectedPassword) =>
              actor ! BCrypt.checkpw(password, expectedPassword)

            case None =>
              actor ! false
          }

        case Failure(e) =>
          actor ! Status.Failure(e)
      }
  }

}

object AuthenticationService {

  private val authenticateTimeout = Timeout(60.seconds)

  def apply(accountDAO: AccountDAO)(implicit actorSystem: ActorSystem): AuthenticationService = {
    val actor = actorSystem.actorOf(
      Props(
        new AuthenticationServiceActor(accountDAO)
      )
    )

    new AuthenticationService(actor)
  }

}

class AuthenticationService private(actor: ActorRef) extends ActorWrapper(actor) {

  def authenticate(email: String, password: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    implicit val timeout = AuthenticationService.authenticateTimeout
    val futureResult = actor ? AuthenticationServiceActor.PasswordAuthenticate(email, password)

    futureResult.map { result =>
      result.asInstanceOf[Boolean]
    }
  }

}
