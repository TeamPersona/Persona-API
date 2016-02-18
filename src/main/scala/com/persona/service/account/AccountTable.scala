package com.persona.service.account

import slick.driver.PostgresDriver.api._
import slick.lifted.Index

case class CreatableAccount(
  id: Option[Int],
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String)

private class AccountTable(tag: Tag) extends Table[CreatableAccount](tag, "accounts") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def givenName = column[String]("given_name")
  def familyName = column[String]("family_name")
  def emailAddress = column[String]("email_address")
  def phoneNumber = column[String]("phone_number")

  def * = (id.?, givenName, familyName, emailAddress, phoneNumber) <> (CreatableAccount.tupled, CreatableAccount.unapply)

  def unique_email_address: Index = index("unique_email_address", emailAddress, unique = true)
  def unique_phone_number: Index = index("unique_phone_number", phoneNumber, unique = true)

}
