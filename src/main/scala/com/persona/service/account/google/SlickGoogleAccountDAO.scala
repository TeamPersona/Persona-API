package com.persona.service.account.google

import com.persona.service.account.{Account, CreatableAccountUtils, AccountTable, AccountDescriptor}

import scala.concurrent.{Future, ExecutionContext}

import slick.driver.PostgresDriver.api._

class SlickGoogleAccountDAO(db: Database) extends GoogleAccountDAO with CreatableAccountUtils {

  private[this] val accounts = TableQuery[AccountTable]
  private[this] val googleAccounts = TableQuery[GoogleAccountTable]

  def exists(googleId: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val query = googleAccounts.filter { googleAccount =>
      googleAccount.googleId === googleId
    }

    db.run(query.exists.result)
  }

  def exists(googleAccountDescriptor: GoogleAccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean] = {
    val join = for {
      (account, googleAccount) <- accounts join googleAccounts on (_.id === _.id)
    } yield(account.emailAddress, account.phoneNumber, googleAccount.googleId)

    val query = join.filter { row =>
      row._1 === googleAccountDescriptor.accountDescriptor.emailAddress ||
      row._2 === googleAccountDescriptor.accountDescriptor.phoneNumber ||
      row._3 === googleAccountDescriptor.googleId
    }

    db.run(query.exists.result)
  }

  def create(googleAccountDescriptor: GoogleAccountDescriptor)(implicit ec: ExecutionContext): Future[Account] = {
    val query = for {
      userId <- (accounts returning accounts.map(_.id)) += toCreatableAccount(googleAccountDescriptor.accountDescriptor)
      _ <- googleAccounts += GoogleAccount(userId, googleAccountDescriptor.googleId)
    } yield userId

    db.run(query.transactionally).map { userId =>
      toAccount(userId, googleAccountDescriptor.accountDescriptor)
    }
  }

}
