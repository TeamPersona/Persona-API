package com.persona.service.authorization

import com.persona.service.account.thirdparty.{ThirdPartyAccount, ThirdPartyAccountTable}
import com.persona.service.account.{Account, AccountTable, CreatableAccountUtils}
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

sealed class UnknownValidateResult extends RuntimeException

class SlickRefreshTokenDAO(db: Database) extends RefreshTokenDAO with CreatableAccountUtils {

  private[this] val refreshTokens = TableQuery[RefreshTokenTable]
  private[this] val accounts = TableQuery[AccountTable]
  private[this] val thirdPartyAccounts = TableQuery[ThirdPartyAccountTable]

  def create(refreshTokenDescriptor: RefreshTokenDescriptor)(implicit ec: ExecutionContext): Future[Unit] = {
    db.run(refreshTokens += refreshTokenDescriptor).map(_ => ())
  }

  def validate(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[(Account, ThirdPartyAccount)]] = {
    val query = refreshTokens.join(accounts).on(_.accountId === _.id)
                             .join(thirdPartyAccounts).on(_._1.thirdPartyAccountId === _.id)
                             .filter(table => table._1._1.token === refreshToken && table._1._1.valid === true)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map { result =>
        val ((_, creatableAccount), thirdPartyAccount) = result

        (toAccount(creatableAccount), thirdPartyAccount)
      }
    }
  }

}
