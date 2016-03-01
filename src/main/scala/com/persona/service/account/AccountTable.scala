package com.persona.service.account

import slick.driver.PostgresDriver.api._
import slick.lifted.Index

case class CreatableAccount(
  id: Option[Int],
  givenName: String,
  familyName: String,
  emailAddress: String,
  phoneNumber: String,
  rewardPoints: Int,
  balance: Int
)

sealed class InvalidAccountOperationException extends RuntimeException

trait CreatableAccountUtils {

  def toCreatableAccount(accountDescriptor: AccountDescriptor): CreatableAccount = {
    CreatableAccount(
      None,
      accountDescriptor.givenName,
      accountDescriptor.familyName,
      accountDescriptor.emailAddress,
      accountDescriptor.phoneNumber,
      0,
      0
    )
  }

  def toAccount(creatableAccount: CreatableAccount): Account = {
    creatableAccount.id.map { accountId =>
      Account(
        accountId,
        creatableAccount.givenName,
        creatableAccount.familyName,
        creatableAccount.emailAddress,
        creatableAccount.phoneNumber,
        creatableAccount.rewardPoints,
        creatableAccount.balance
      )
    } getOrElse {
      throw new InvalidAccountOperationException
    }
  }

  def toAccount(id: Int, accountDescriptor: AccountDescriptor): Account = {
    Account(
      id,
      accountDescriptor.givenName,
      accountDescriptor.familyName,
      accountDescriptor.emailAddress,
      accountDescriptor.phoneNumber,
      0,
      0
    )
  }

}

class AccountTable(tag: Tag) extends Table[CreatableAccount](tag, "accounts") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def givenName = column[String]("given_name")
  def familyName = column[String]("family_name")
  def emailAddress = column[String]("email_address")
  def phoneNumber = column[String]("phone_number")
  def rewardPoints = column[Int]("reward_points")
  def balance = column[Int]("balance")

  def * = (
    id.?,
    givenName,
    familyName,
    emailAddress,
    phoneNumber,
    rewardPoints,
    balance
    ) <>
    (
      CreatableAccount.tupled,
      CreatableAccount.unapply
    )

  def uniqueEmailAddress: Index = index("unique_email_address", emailAddress, unique = true)
  def uniquePhoneNumber: Index = index("unique_phone_number", phoneNumber, unique = true)

}
