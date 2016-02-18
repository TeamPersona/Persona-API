package com.persona.service.account

import slick.driver.PostgresDriver.api._

case class VerifiableAccount(id: Int, password: String, salt: String)

private class PasswordTable(tag: Tag) extends Table[VerifiableAccount](tag, "passwords") {

  def id = column[Int]("id")
  def password = column[String]("password")
  def salt = column[String]("salt")

  def * = (id, password, salt) <> (VerifiableAccount.tupled, VerifiableAccount.unapply)

  def fk = foreignKey("account_fk", id, TableQuery[AccountTable])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

}
