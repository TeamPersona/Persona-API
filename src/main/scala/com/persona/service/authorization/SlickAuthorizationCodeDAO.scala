package com.persona.service.authorization

import com.persona.service.account.{CreatableAccountUtils, AccountTable, Account}
import com.persona.service.account.thirdparty.{ThirdPartyAccountTable, ThirdPartyAccount}
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class SlickAuthorizationCodeDAO(db: Database) extends AuthorizationCodeDAO with CreatableAccountUtils {

  private[this] val authorizationCodes = TableQuery[AuthorizationCodeTable]
  private[this] val accounts = TableQuery[AccountTable]
  private[this] val thirdPartyAccounts = TableQuery[ThirdPartyAccountTable]

  def create(authorizationCode: AuthorizationCode)(implicit ec: ExecutionContext): Future[Unit] = {
    db.run(authorizationCodes += authorizationCode).map(_ => ())
  }

  def validate(code: String)(implicit ec: ExecutionContext): Future[Option[(Account, ThirdPartyAccount)]] = {
    val query = authorizationCodes.join(accounts).on((authorizationCode, account) => authorizationCode.accountId === account.id)
                                  .join(thirdPartyAccounts).on(_._1.thirdPartyAccountId === _.id)
                                  .filter(table => table._1._1.code === code && table._1._1.valid === true)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map { result =>
        val ((_, creatableAccount), thirdPartyAccount) = result

        (toAccount(creatableAccount), thirdPartyAccount)
      }
    }
  }

  def invalidate(authorizationCode: AuthorizationCode)(implicit ec: ExecutionContext): Future[Unit] = {
    val query = authorizationCodes.filter(authCode => authCode.code === authorizationCode.code)
                                  .map(authCode => authCode.valid)
                                  .update(false)

    db.run(query).map(_ => ())
  }

}
