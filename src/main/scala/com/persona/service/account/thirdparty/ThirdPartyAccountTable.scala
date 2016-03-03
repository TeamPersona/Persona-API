package com.persona.service.account.thirdparty

import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime

import slick.driver.PostgresDriver.api._
import slick.lifted.Index

class ThirdPartyAccountTable(tag: Tag) extends Table[ThirdPartyAccount](tag, "third_party_accounts") {

  def id = column[String]("id", O.PrimaryKey)
  def secret = column[String]("secret")
  def creationTime = column[DateTime]("creation_time")

  def * = (id, secret, creationTime) <> (ThirdPartyAccount.tupled, ThirdPartyAccount.unapply)

}
