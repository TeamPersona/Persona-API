package com.persona.service.account

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.persona.service.account.thirdparty.{ThirdPartyAccount, ThirdPartyAccountDAO}
import com.persona.util.actor.ActorWrapper
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalaz.Scalaz._
import scalaz.ValidationNel

object AccountServiceActor {

  case class Create(accountDescriptor: AccountDescriptor, password: String)
  case class Retrieve(id: Int)
  case class RetrieveThirdParty(id: String)

}

class AccountServiceActor(
  accountDAO: AccountDAO,
  thirdPartyAccountDAO: ThirdPartyAccountDAO,
  passwordLogRounds: Int)
  extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case AccountServiceActor.Create(accountDescriptor, password) =>
      handleCreate(accountDescriptor, password, sender)

    case AccountServiceActor.Retrieve(id: Int) =>
      handleRetrieve(id, sender)

    case AccountServiceActor.RetrieveThirdParty(id: String) =>
      handleRetrieveThirdParty(id, sender)
  }

  private[this] def handleCreate(accountDescriptor: AccountDescriptor, password: String, actor: ActorRef) = {
    accountDAO.exists(accountDescriptor).onComplete {
      case Success(exists) =>
        if(exists) {
          actor ! (new AccountAlreadyExistsError).failureNel
        } else {
          val salt = BCrypt.gensalt(passwordLogRounds)
          val hashedPassword = BCrypt.hashpw(password, salt)

          accountDAO.create(accountDescriptor, hashedPassword).onComplete {
            case Success(result) => actor ! result.successNel
            case Failure(e) => actor ! Status.Failure(e)
          }
        }

      case Failure(e) => actor ! Status.Failure(e)
    }
  }

  private[this] def handleRetrieve(id: Int, actor: ActorRef) = {
    accountDAO.retrieve(id).pipeTo(actor)
  }

  private[this] def handleRetrieveThirdParty(id: String, actor: ActorRef) = {
    thirdPartyAccountDAO.retrieve(id).pipeTo(actor)
  }

}

object AccountService {

  private val createTimeout = Timeout(60.seconds)
  private val retrieveTimeout = Timeout(60.seconds)

  def apply(accountDAO: AccountDAO, thirdPartyAccountDAO: ThirdPartyAccountDAO, passwordLogRounds: Int)
           (implicit actorSystem: ActorSystem): AccountService = {
    val actor = actorSystem.actorOf(
      Props(
        new AccountServiceActor(accountDAO, thirdPartyAccountDAO, passwordLogRounds)
      )
    )

    new AccountService(actor)
  }

}

class AccountService private(actor: ActorRef) extends ActorWrapper(actor) {

  def create(accountDescriptor: AccountDescriptor, password: String)
            (implicit ec: ExecutionContext): Future[ValidationNel[AccountValidationError, Account]] = {
    implicit val timeout = AccountService.createTimeout
    val futureResult = actor ? AccountServiceActor.Create(accountDescriptor, password)

    futureResult.map { result =>
      result.asInstanceOf[ValidationNel[AccountValidationError, Account]]
    }
  }

  def retrieve(id: Int)(implicit ec: ExecutionContext): Future[Option[Account]] = {
    implicit val timeout = AccountService.retrieveTimeout
    val futureResult = actor ? AccountServiceActor.Retrieve(id)

    futureResult.map { result =>
      result.asInstanceOf[Option[Account]]
    }
  }

  def retrieveThirdPartyAccount(id: String)(implicit ec: ExecutionContext): Future[Option[ThirdPartyAccount]] = {
    implicit val timeout = AccountService.retrieveTimeout
    val futureResult = actor ? AccountServiceActor.RetrieveThirdParty(id)

    futureResult.map { result =>
      result.asInstanceOf[Option[ThirdPartyAccount]]
    }
  }

}
