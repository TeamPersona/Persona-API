package com.persona.service.account

import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

sealed class InvalidAccountOperationException extends RuntimeException

class SlickAccountDAO(db: Database) extends AccountDAO {

  private[this] val accounts = TableQuery[AccountTable]
  private[this] val passwords = TableQuery[PasswordTable]

  def retrieve(id: Int)(implicit ec: ExecutionContext): Future[Option[Account]] = {
    val query = accounts.filter { account =>
      account.id === id
    }

    db.run(query.result.headOption).map { creatableAccountOption =>
      creatableAccountOption.map { creatableAccount =>
        toAccount(creatableAccount)
      }
    }
  }

  def exists(accountDescriptor: AccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean] = {
    val query = accounts.filter { account =>
      account.emailAddress === accountDescriptor.emailAddress ||
      account.phoneNumber === accountDescriptor.phoneNumber
    }

    db.run(query.exists.result)
  }

  def create(accountDescriptor: AccountDescriptor, hashedPassword: String)(implicit ec: ExecutionContext): Future[Unit] = {
    val query = (for {
      userId <- (accounts returning accounts.map(_.id)) += toCreatableAccount(accountDescriptor)
      _ <- passwords += VerifiableAccount(userId, hashedPassword)
    } yield()).transactionally

    db.run(query).map(_ => ())
  }

  private[this] def toCreatableAccount(accountDescriptor: AccountDescriptor): CreatableAccount = {
    CreatableAccount(
      None,
      accountDescriptor.givenName,
      accountDescriptor.familyName,
      accountDescriptor.emailAddress,
      accountDescriptor.phoneNumber
    )
  }

  private[this] def toAccount(creatableAccount: CreatableAccount): Account = {
    creatableAccount.id.map { accountId =>
      Account(
        accountId,
        creatableAccount.givenName,
        creatableAccount.familyName,
        creatableAccount.emailAddress,
        creatableAccount.phoneNumber
      )
    } getOrElse {
      throw new InvalidAccountOperationException
    }
  }

}
