package com.persona.service.account.thirdparty

import java.security.SecureRandom

import com.persona.util.security.SecureAlphanumericStringGenerator
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import scala.concurrent.{Future, ExecutionContext}

private class InvalidThirdPartyAccountException extends RuntimeException

class SlickThirdPartyAccountDAO(
  db: Database,
  random: SecureRandom,
  stringGenerator: SecureAlphanumericStringGenerator)
  extends ThirdPartyAccountDAO {

  private[this] val thirdPartyAccounts = TableQuery[ThirdPartyAccountTable]

  def exists(id: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val query = thirdPartyAccounts.filter { thirdPartyAccount =>
      thirdPartyAccount.id === id
    }

    db.run(query.exists.result)
  }

  def retrieve(id: String)(implicit ec: ExecutionContext): Future[Option[ThirdPartyAccount]] = {
    val query = thirdPartyAccounts.filter { thirdPartyAccount =>
      thirdPartyAccount.id === id
    }

    db.run(query.result.headOption)
  }

  def create()(implicit ec: ExecutionContext): Future[ThirdPartyAccount] = {
    val randomLong = toUnsignedLong(random.nextLong())
    val clientId = randomLong + ".apps.uwpersona.com"
    val clientSecret = stringGenerator.generate
    val thirdPartyAccount = ThirdPartyAccount(clientId, clientSecret, DateTime.now)

    db.run(thirdPartyAccounts += thirdPartyAccount).map { _ =>
      thirdPartyAccount
    }
  }

  private[this] def toUnsignedLong(l: Long) = {
    (BigInt(l >>> 1) << 1) + (l & 1)
  }

}
