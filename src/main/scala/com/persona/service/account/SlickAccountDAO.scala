package com.persona.service.account

import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class SlickAccountDAO(db: Database) extends AccountDAO with CreatableAccountUtils {

  private[this] val accounts = TableQuery[AccountTable]
  private[this] val passwords = TableQuery[PasswordTable]

  def retrievePassword(email: String)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val join = for {
      (account, password) <- accounts join passwords on (_.id === _.id)
    } yield(account.emailAddress, password.password)

    val where = join.filter { row =>
      row._1 === email
    }

    val select = where.map { row =>
      row._2
    }

    db.run(select.result.headOption)
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

}
