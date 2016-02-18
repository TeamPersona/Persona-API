package com.persona.service.account.google

import com.persona.service.account.AccountTable
import slick.driver.PostgresDriver.api._
import slick.lifted.Index

case class GoogleAccount(id: Int, googleId: String)

private class GoogleAccountTable(tag: Tag) extends Table[GoogleAccount](tag, "google_accounts") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def googleId = column[String]("google_id")

  def * = (id, googleId) <> (GoogleAccount.tupled, GoogleAccount.unapply)

  def uniqueGoogleId: Index = index("unique_google_id", googleId, unique = true)
  def fk = foreignKey("accounts_fk", id, TableQuery[AccountTable])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

}
