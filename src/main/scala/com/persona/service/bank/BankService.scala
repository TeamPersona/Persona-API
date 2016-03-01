package com.persona.service.bank

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import com.persona.service.account.{AccountService, Account}
import com.persona.util.actor.ActorWrapper

import com.websudos.phantom.dsl.ResultSet

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import scalaz.Scalaz._
import scalaz.ValidationNel

private object BankServiceActor {

  private case class ValidateRetrievedInformation(data: Seq[DataItem], actor: ActorRef)
  case class Retrieve(account: Account)
  case class Insert(account: Account, dataItem: DataItem)
  case class Has(account: Account, data: List[(String, String)])

}

private class BankServiceActor(
  bankDAO: BankDAO,
  dataItemValidator: DataItemValidator,
  accountService: AccountService,
  dataSchemaManager: DataSchemaManager)
  extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case BankServiceActor.ValidateRetrievedInformation(data, actor) =>
      handleValidateRetrievedInformation(data, actor)

    case BankServiceActor.Retrieve(account) =>
      handleRetrieve(account, sender)

    case BankServiceActor.Insert(account, dataItem) =>
      handleInsert(account, dataItem, sender)

    case BankServiceActor.Has(account, data) =>
      handleHas(account, data, sender)
  }

  private[this] def handleValidateRetrievedInformation(data: Seq[DataItem], actor: ActorRef) = {
    // We're retrieving data that's already been stored, so it should be valid data
    // Still, we need to double check
    val validData = data.forall { dataItem =>
      dataItemValidator.validate(dataItem).isSuccess
    }

    if(validData) {
      actor ! data
    } else {
      actor ! Status.Failure(new InvalidDataException("Invalid stored data"))
    }
  }

  private[this] def handleRetrieve(account: Account, actor: ActorRef) = {
    bankDAO.retrieve(account).onComplete {
      case Success(data) =>
        self ! BankServiceActor.ValidateRetrievedInformation(data, actor)

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

  private[this] def handleInsert(account: Account, dataItem: DataItem, actor: ActorRef) = {
    val validationResult = dataItemValidator.validate(dataItem)

    if(validationResult.isSuccess) {
      bankDAO.has(account, dataItem.category, dataItem.subcategory).onComplete {
        case Success(hasData) =>
          // If this is the first item of its type, then add reward points to the account
          val addRewardPointsFutureOption = if(hasData) {
            None
          } else {
            dataSchemaManager.schema(dataItem.category, dataItem.subcategory).map { schema =>
              accountService.addRewardPoints(account, schema.rewardPoints)
            }
          }

          // No matter what, insert the item
          val insertDataFuture = bankDAO.insert(account, dataItem)

          // We have either 1 or 2 requests in flight
          // If there are 2 requests, combine them into 1 request
          val combinedFuture = addRewardPointsFutureOption.map { addRewardPointsFuture =>
            for {
              insertDataResult <- insertDataFuture
              _ <- addRewardPointsFuture
            } yield insertDataResult
          } getOrElse {
            insertDataFuture
          }

          // When the in flight requests are done, return the result to the sender
          combinedFuture.onComplete {
            case Success(result) =>
              actor ! result.successNel

            case Failure(e) =>
              actor ! Status.Failure(e)
          }

        case Failure(e) =>
          actor ! Status.Failure(e)
      }
    } else {
      actor ! validationResult
    }
  }

  private[this] def handleHas(account: Account, data: List[(String, String)], actor: ActorRef) = {
    bankDAO.has(account, data).pipeTo(actor)
  }

}

object BankService {

  private val listTimeout = Timeout(60.seconds)
  private val saveTimeout = Timeout(60.seconds)
  private val hasTimeout = Timeout(60.seconds)

  def apply(bankDAO: BankDAO, dataItemValidator: DataItemValidator, accountService: AccountService, dataSchemaManager: DataSchemaManager)
           (implicit actorSystem: ActorSystem): BankService = {
    val actor = actorSystem.actorOf(
      Props(
        new BankServiceActor(bankDAO, dataItemValidator, accountService, dataSchemaManager)
      )
    )

    new BankService(actor)
  }

}

class BankService private(actor: ActorRef) extends ActorWrapper(actor) {

  def retrieve(account: Account)(implicit ec: ExecutionContext): Future[Seq[DataItem]] = {
    implicit val timeout = BankService.listTimeout
    val futureResult = actor ? BankServiceActor.Retrieve(account)

    futureResult.map { result =>
      result.asInstanceOf[Seq[DataItem]]
    }
  }

  def insert(account: Account, dataItem: DataItem)
            (implicit ec: ExecutionContext): Future[ValidationNel[DataItemValidationError, ResultSet]] = {
    implicit val timeout = BankService.saveTimeout
    val futureResult = actor ? BankServiceActor.Insert(account, dataItem)

    futureResult.map { validationResult =>
      validationResult.asInstanceOf[ValidationNel[DataItemValidationError, ResultSet]]
    }
  }

  def has(account: Account, data: List[(String, String)])(implicit ec: ExecutionContext): Future[Boolean] = {
    implicit val timeout = BankService.hasTimeout
    val futureResult = actor ? BankServiceActor.Has(account, data)

    futureResult.map { result =>
      result.asInstanceOf[Boolean]
    }
  }

  def has(account: Account, category: String, subcategory: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    has(account, List((category, subcategory)))
  }

}
