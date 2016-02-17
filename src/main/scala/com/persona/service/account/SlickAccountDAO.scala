package com.persona.service.account

import slick.driver.PostgresDriver.api._
import slick.lifted.Index

import scala.concurrent.{ExecutionContext, Future}

private class AccountTable(tag: Tag) extends Table[RawAccount](tag, "accounts") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def givenName = column[String]("given_name")
  def familyName = column[String]("family_name")
  def emailAddress = column[String]("email_address")
  def phoneNumber = column[String]("phone_number")

  def * = (id.?, givenName, familyName, emailAddress, phoneNumber) <> (RawAccount.tupled, RawAccount.unapply)

  def unique_email_address: Index = index("unique_email_address", emailAddress, unique = true)
  def unique_phone_number: Index = index("unique_phone_number", phoneNumber, unique = true)

}

class SlickAccountDAO(db: Database) extends AccountDAO {

  private[this] val accounts = TableQuery[AccountTable]

  def retrieve(id: Int)(implicit ec: ExecutionContext): Future[Option[Account]] = {
    val query = accounts.filter { account =>
      account.id === id
    }

    db.run(query.result.headOption).map { rawAccountOption =>
      rawAccountOption.map { rawAccount =>
        rawAccount.toAccount
      }
    }
  }

  def exists(accountDescriptor: AccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean] = {
    val query = accounts.filter { account =>
      account.emailAddress === accountDescriptor.emailAddress &&
      account.phoneNumber === accountDescriptor.phoneNumber
    }

    db.run(query.exists.result)
  }

  def create(accountDescriptor: AccountDescriptor)(implicit ec: ExecutionContext): Future[Unit] = {
    db.run(accounts += accountDescriptor.toRawAccount).map(_ => ())
  }

}
