package com.persona.service.authorization

import com.persona.service.account.AccountTable
import com.persona.service.account.thirdparty.ThirdPartyAccountTable

import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

class AuthorizationCodeTable(tag: Tag) extends Table[AuthorizationCode](tag, "authorization_codes") {

  def code: Rep[String] = column[String]("token", O.PrimaryKey)
  def accountId: Rep[Int] = column[Int]("account_id")
  def thirdPartyAccountId: Rep[String] = column[String]("third_party_account_id")
  def valid: Rep[Boolean] = column[Boolean]("valid")

  def * : ProvenShape[AuthorizationCode] = (
    code,
    accountId,
    thirdPartyAccountId,
    valid
    ) <> (
    AuthorizationCode.tupled,
    AuthorizationCode.unapply
    )

  def accountsFk = foreignKey(
    "accounts_fk",
    accountId,
    TableQuery[AccountTable]
  )(
    _.id,
    onUpdate=ForeignKeyAction.Cascade,
    onDelete=ForeignKeyAction.Cascade
  )

  def thirdPartyAccountFk = foreignKey(
    "third_party_accounts_fk",
    thirdPartyAccountId,
    TableQuery[ThirdPartyAccountTable]
  )(
    _.id,
    onUpdate=ForeignKeyAction.Cascade,
    onDelete=ForeignKeyAction.Cascade
  )
}
