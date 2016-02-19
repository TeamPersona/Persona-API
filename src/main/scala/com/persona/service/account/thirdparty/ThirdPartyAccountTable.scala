package com.persona.service.account.thirdparty

import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime

import slick.driver.PostgresDriver.api._

private class ThirdPartyAccountTable(tag: Tag) extends Table[ThirdPartyAccount](tag, "third_party_accounts") {

  def clientId = column[String]("client_id", O.PrimaryKey)
  def creationTime = column[DateTime]("creation_time")

  def * = (clientId, creationTime) <> (ThirdPartyAccount.tupled, ThirdPartyAccount.unapply)

}
