package com.persona.service.account

import slick.driver.PostgresDriver.api._

case class VerifiableAccount(id: Int, password: String)

private class PasswordTable(tag: Tag) extends Table[VerifiableAccount](tag, "passwords") {

  def id = column[Int]("id", O.PrimaryKey)
  def password = column[String]("password")

  def * = (id, password) <> (VerifiableAccount.tupled, VerifiableAccount.unapply)

  def fk = foreignKey("accounts_fk", id, TableQuery[AccountTable])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

}
