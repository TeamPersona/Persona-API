package com.persona.service.account.thirdparty

import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime

import slick.driver.PostgresDriver.api._

class ThirdPartyAccountTable(tag: Tag) extends Table[ThirdPartyAccount](tag, "third_party_accounts") {

  def id = column[String]("client_id", O.PrimaryKey)
  def creationTime = column[DateTime]("creation_time")

  def * = (id, creationTime) <> (ThirdPartyAccount.tupled, ThirdPartyAccount.unapply)

}
