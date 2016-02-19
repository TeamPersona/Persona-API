package com.persona.service.account.thirdparty

import java.security.SecureRandom

import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import scala.concurrent.{Future, ExecutionContext}

private class InvalidThirdPartyAccountException extends RuntimeException

class SlickThirdPartAccountDAO(db: Database, random: SecureRandom) extends ThirdPartyAccountDAO {

  private[this] val thirdPartyAccounts = TableQuery[ThirdPartyAccountTable]

  def exists(clientId: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val query = thirdPartyAccounts.filter { thirdPartyAccount =>
      thirdPartyAccount.clientId === clientId
    }

    db.run(query.exists.result)
  }

  def retrieve(clientId: String)(implicit ec: ExecutionContext): Future[Option[ThirdPartyAccount]] = {
    val query = thirdPartyAccounts.filter { thirdPartyAccount =>
      thirdPartyAccount.clientId === clientId
    }

    db.run(query.result.headOption)
  }

  def create()(implicit ec: ExecutionContext): Future[ThirdPartyAccount] = {
    val randomLong = toUnsignedLong(random.nextLong())
    val clientId = randomLong + ".apps.uwpersona.com"
    val thirdPartyAccount = ThirdPartyAccount(clientId, DateTime.now)

    db.run(thirdPartyAccounts += thirdPartyAccount).map { _ =>
      thirdPartyAccount
    }
  }

  private[this] def toUnsignedLong(l: Long) = {
    (BigInt(l >>> 1) << 1) + (l & 1)
  }

}
