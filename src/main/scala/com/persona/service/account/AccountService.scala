package com.persona.service.account

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.persona.util.actor.ActorWrapper

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import scalaz.Scalaz._
import scalaz.ValidationNel

object AccountServiceActor {

  case class Create(accountDescriptor: AccountDescriptor)

}

class AccountServiceActor(accountDAO: AccountDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case AccountServiceActor.Create(accountDescriptor) =>
      val actor = sender

      accountDAO.exists(accountDescriptor).onComplete {
        case Success(exists) =>
          if(exists) {
            actor ! (new AccountAlreadyExistsError).failureNel
          } else {
            accountDAO.create(accountDescriptor).onComplete {
              case Success(result) => actor ! result.successNel
              case Failure(e) => actor ! Status.Failure(e)
            }
          }

        case Failure(e) => actor ! Status.Failure(e)
      }
  }

}

object AccountService {

  private val createTimeout = Timeout(60.seconds)

  def apply(accountDAO: AccountDAO)(implicit actorSystem: ActorSystem): AccountService = {
    val actor = actorSystem.actorOf(Props(new AccountServiceActor(accountDAO)))

    new AccountService(actor)
  }

}

class AccountService private(actor: ActorRef) extends ActorWrapper(actor) {

  def create(accountDescriptor: AccountDescriptor)
            (implicit ec: ExecutionContext): Future[ValidationNel[AccountValidationError, Unit]] = {
    implicit val timeout = AccountService.createTimeout
    val futureResult = actor ? AccountServiceActor.Create(accountDescriptor)

    futureResult.map { result =>
      result.asInstanceOf[ValidationNel[AccountValidationError, Unit]]
    }
  }

}