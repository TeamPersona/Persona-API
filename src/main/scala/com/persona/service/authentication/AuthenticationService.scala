package com.persona.service.authentication

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import com.persona.service.account.{Account, AccountDAO}
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

      accountDAO.retrieve(email).onComplete {
        case Success(resultOption) =>
          resultOption match {
            case Some(result) =>
              val (account, expectedPassword) = result

              if(BCrypt.checkpw(password, expectedPassword)) {
                actor ! Some(account)
              } else {
                actor ! None
              }

            case None =>
              actor ! None
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

  def authenticate(email: String, password: String)(implicit ec: ExecutionContext): Future[Option[Account]] = {
    implicit val timeout = AuthenticationService.authenticateTimeout
    val futureResult = actor ? AuthenticationServiceActor.PasswordAuthenticate(email, password)

    futureResult.map { result =>
      result.asInstanceOf[Option[Account]]
    }
  }

}
