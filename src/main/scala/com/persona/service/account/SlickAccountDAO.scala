package com.persona.service.account

import slick.driver.PostgresDriver.api._
import slick.lifted.Index

case class TestAccount(id: Option[Long], email: String)

private class AccountTable(tag: Tag) extends Table[TestAccount](tag, "account") {

  def id = column[Long]("account_id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")

  def * = (id.?, email) <> (TestAccount.tupled, TestAccount.unapply)

  def unique_email: Index = index("unique_email", email, unique = true)

}

class SlickAccountDAO(db: Database) extends AccountDAO {

  private[this] val account = TableQuery[AccountTable]
  Console.println("ADDING DATA")
  db.run(account += TestAccount(None, "blah"))

}
