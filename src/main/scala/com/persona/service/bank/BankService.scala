package com.persona.service.bank

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import com.persona.service.account.Account
import com.persona.util.actor.ActorWrapper

import com.websudos.phantom.dsl.ResultSet

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import scalaz.Scalaz._
import scalaz.ValidationNel

private object BankServiceActor {

  private case class ValidateRetrievedInformation(actor: ActorRef, data: Seq[DataItem])
  case class ListInformation(account: Account)
  case class SaveInformation(account: Account, dataItem: DataItem)

}

private class BankServiceActor(
  bankDAO: BankDAO,
  dataItemValidator: DataItemValidator)
  extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case BankServiceActor.ValidateRetrievedInformation(actor, data) =>
      // We're retrieving data that's already been stored, so it should be valid data
      // Still, we need to double check
      val validData = data.forall { dataItem =>
        dataItemValidator.validate(dataItem).isSuccess
      }

      if(validData) actor ! data
      else actor ! Status.Failure(new InvalidDataException("Invalid stored data"))

    case BankServiceActor.ListInformation(account) =>
      val actor = sender

      bankDAO.listInformation(account).onComplete {
        case Success(data) => self ! BankServiceActor.ValidateRetrievedInformation(actor, data)
        case Failure(e) => actor ! Status.Failure(e)
      }

    case BankServiceActor.SaveInformation(account, dataItem) =>
      val actor = sender
      val validationResult = dataItemValidator.validate(dataItem)

      if(validationResult.isSuccess) {
        bankDAO.saveInformation(account, dataItem).onComplete {
          case Success(result) => actor ! result.successNel
          case Failure(e) => actor ! Status.Failure(e)
        }
      } else {
        actor ! validationResult
      }
  }

}

object BankService {

  private val listTimeout = Timeout(60.seconds)
  private val saveTimeout = Timeout(60.seconds)

  def apply(bankDAO: BankDAO, dataItemValidator: DataItemValidator)
           (implicit actorSystem: ActorSystem): BankService = {
    val actor = actorSystem.actorOf(
      Props(
        new BankServiceActor(bankDAO, dataItemValidator)
      )
    )

    new BankService(actor)
  }

}

class BankService private(actor: ActorRef) extends ActorWrapper(actor) {

  def listInformation(account: Account)(implicit ec: ExecutionContext): Future[Seq[DataItem]] = {
    implicit val timeout = BankService.listTimeout
    val futureResult = actor ? BankServiceActor.ListInformation(account)

    futureResult.map { result =>
      result.asInstanceOf[Seq[DataItem]]
    }
  }

  def saveInformation(account: Account, dataItem: DataItem)
                     (implicit ec: ExecutionContext): Future[ValidationNel[DataItemValidationError, ResultSet]] = {
    implicit val validateTimeout = BankService.saveTimeout
    val futureResult = actor ? BankServiceActor.SaveInformation(account, dataItem)

    futureResult.map { validationResult =>
      validationResult.asInstanceOf[ValidationNel[DataItemValidationError, ResultSet]]
    }
  }

}
