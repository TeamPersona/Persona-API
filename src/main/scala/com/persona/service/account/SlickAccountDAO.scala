package com.persona.service.account

import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class SlickAccountDAO(db: Database) extends AccountDAO with CreatableAccountUtils {

  private[this] val accounts = TableQuery[AccountTable]
  private[this] val passwords = TableQuery[PasswordTable]

  def retrieve(id: Int)(implicit ec: ExecutionContext): Future[Option[Account]] = {
    val query = accounts.filter { account =>
      account.id === id
    }

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map { result =>
        toAccount(result)
      }
    }
  }

  def retrieveByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[(Account, String)]] = {
    val query = accounts.join(passwords).on(_.id === _.id)
                        .filter(table => table._1.emailAddress === email)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map { result =>
        val (creatableAccount, verifiableAccount) = result

        (toAccount(creatableAccount), verifiableAccount.password)
      }
    }
  }

  def exists(accountDescriptor: AccountDescriptor)(implicit ec: ExecutionContext): Future[Boolean] = {
    val query = accounts.filter { account =>
      account.emailAddress === accountDescriptor.emailAddress ||
      account.phoneNumber === accountDescriptor.phoneNumber
    }

    db.run(query.exists.result)
  }

  def create(accountDescriptor: AccountDescriptor, hashedPassword: String)(implicit ec: ExecutionContext): Future[Account] = {
    val query = for {
      userId <- (accounts returning accounts.map(_.id)) += toCreatableAccount(accountDescriptor)
      _ <- passwords += VerifiableAccount(userId, hashedPassword)
    } yield userId

    db.run(query.transactionally).map { userId =>
      toAccount(userId, accountDescriptor)
    }
  }

  def updateRewardPoints(account: Account, points: Int)(implicit ec: ExecutionContext): Future[Int] = {
    val query = accounts.filter(acc => acc.id === account.id)
                        .map(acc => acc.balance)
                        .update(points)

    db.run(query)
  }

}
