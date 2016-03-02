package com.persona.service.authorization

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.persona.service.account.{AccountService, Account}
import com.persona.service.account.thirdparty.ThirdPartyAccount
import com.persona.util.actor.ActorWrapper

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AuthorizationServiceActor {

  case class Authorize(account: Account, thirdPartyAccount: ThirdPartyAccount)
  case class Validate(accessToken: String)
  case class GenerateAuthorizationCode(account: Account, thirdPartyAccount: ThirdPartyAccount)
  case class AuthorizationCodeGrant(authorizationCode: String, clientId: String)
  case class RefreshTokenGrant(refreshToken: String, clientId: String)

}

class AuthorizationServiceActor(
  accountService: AccountService,
  accessTokenGenerator: AccessTokenGenerator,
  accessTokenExpirationTime: Int,
  stringGenerator: SecureAlphanumericStringGenerator,
  authorizationCodeDAO: AuthorizationCodeDAO,
  refreshTokenDAO: RefreshTokenDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case AuthorizationServiceActor.Authorize(account, thirdPartyAccount) =>
      handleAuthorization(account, thirdPartyAccount, sender)

    case AuthorizationServiceActor.Validate(accessToken) =>
      handleValidation(accessToken, sender)

    case AuthorizationServiceActor.GenerateAuthorizationCode(account, thirdPartyAccount) =>
      handleGenerateAuthorizationCode(account, thirdPartyAccount, sender)

    case AuthorizationServiceActor.AuthorizationCodeGrant(authorizationCode, clientId) =>
      // TODO - Generate access token and refresh token (For supporting third parties)

    case AuthorizationServiceActor.RefreshTokenGrant(refreshToken, thirdPartyAccount) =>
      handleRefreshTokenGrant(refreshToken, thirdPartyAccount, sender)
  }

  private[this] def generateAccessToken(account: Account, thirdPartyAccount: ThirdPartyAccount) = {
    // Generator expects expiration time in millis
    // OAuth expects expiration time in seconds
    accessTokenGenerator.generate(account, thirdPartyAccount, accessTokenExpirationTime * 1000)
  }

  private[this] def handleAuthorization(account: Account, thirdPartyAccount: ThirdPartyAccount, actor: ActorRef) = {
    val accessToken = generateAccessToken(account, thirdPartyAccount)
    val refreshToken = stringGenerator.generate
    val refreshTokenDescriptor = RefreshTokenDescriptor(refreshToken, account.id, thirdPartyAccount.id)

    refreshTokenDAO.create(refreshTokenDescriptor).onComplete {
      case Success(_) =>
        actor ! AuthorizationResult(accessToken, accessTokenExpirationTime, Some(refreshToken))

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

  private[this] def handleValidation(accessToken: String, actor: ActorRef) = {
    accessTokenGenerator.verify(accessToken) match {
      case Some((accountId, thirdPartyAccountId)) =>
        val futureAccountOption = accountService.retrieve(accountId)
        val futureThirdPartyAccountOption = accountService.retrieveThirdPartyAccount(thirdPartyAccountId)

        val futureResult = for {
          accOption <- futureAccountOption
          thirdPartyAccOption <- futureThirdPartyAccountOption
        } yield {
          accOption.flatMap { acc =>
            thirdPartyAccOption.map { thirdPartyAcc =>
              (acc, thirdPartyAcc)
            }
          }
        }

        futureResult.onComplete {
          case Success(accountsOption) =>
            actor ! accountsOption

          case Failure(e) =>
            actor ! Status.Failure(e)
        }


      case None =>
        actor ! None
    }
  }

  private[this] def handleGenerateAuthorizationCode(account: Account, thirdPartyAccount: ThirdPartyAccount, actor: ActorRef) = {
    val code = stringGenerator.generate
    val authorizationCode = AuthorizationCode(code, account.id, thirdPartyAccount.id)

    authorizationCodeDAO.create(authorizationCode).onComplete {
      case Success(_) =>
        actor ! authorizationCode

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

  private[this] def handleRefreshTokenGrant(refreshToken: String, clientId: String, actor: ActorRef) = {
    refreshTokenDAO.validate(refreshToken).onComplete {
      case Success(resultOption) =>
        resultOption match {
          case Some(result) =>
            val (account, thirdPartyAccount) = result

            if(thirdPartyAccount.id == clientId) {
              val accessToken = generateAccessToken(account, thirdPartyAccount)

              actor ! Some(AuthorizationResult(accessToken, accessTokenExpirationTime))
            } else {
              actor ! None
            }

          case None =>
            actor ! None
        }

      case Failure(e) =>
        actor ! Status.Failure(e)
    }
  }

}

object AuthorizationService {

  private val authorizeTimeout = Timeout(60.seconds)
  private val validateTimeout = Timeout(60.seconds)
  private val generateAuthorizationCodeTimeout = Timeout(60.seconds)
  private val authorizationCodeGrantTimeout = Timeout(60.seconds)
  private val refreshTokenGrantTimeout = Timeout(60.seconds)

  def apply(accountService: AccountService,
            accessTokenGenerator: AccessTokenGenerator, accessTokenExpirationTime: Int,
            stringGenerator: SecureAlphanumericStringGenerator,
            authorizationCodeDAO: AuthorizationCodeDAO,
            refreshTokenDAO: RefreshTokenDAO)
           (implicit actorSystem: ActorSystem): AuthorizationService = {
    val actor = actorSystem.actorOf(
      Props(
        new AuthorizationServiceActor(
          accountService,
          accessTokenGenerator,
          accessTokenExpirationTime,
          stringGenerator,
          authorizationCodeDAO,
          refreshTokenDAO
        )
      )
    )

    new AuthorizationService(actor)
  }

}

class AuthorizationService private(actor: ActorRef) extends ActorWrapper(actor) {

  def authorize(account: Account, thirdPartyAccount: ThirdPartyAccount)(implicit ec: ExecutionContext): Future[AuthorizationResult] = {
    implicit val timeout = AuthorizationService.authorizeTimeout
    val futureResult = actor ? AuthorizationServiceActor.Authorize(account, thirdPartyAccount)

    futureResult.map { result =>
      result.asInstanceOf[AuthorizationResult]
    }
  }

  def validate(accessToken: String)(implicit ec: ExecutionContext): Future[Option[(Account, ThirdPartyAccount)]] = {
    implicit val timeout = AuthorizationService.validateTimeout
    val futureResult = actor ? AuthorizationServiceActor.Validate(accessToken)

    futureResult.map { result =>
      result.asInstanceOf[Option[(Account, ThirdPartyAccount)]]
    }
  }

  def generateAuthorizationCode(account: Account, thirdPartyAccount: ThirdPartyAccount)(implicit ec: ExecutionContext): Future[AuthorizationCode] = {
    implicit val timeout = AuthorizationService.generateAuthorizationCodeTimeout
    val futureResult = actor ? AuthorizationServiceActor.GenerateAuthorizationCode(account, thirdPartyAccount)

    futureResult.map { result =>
      result.asInstanceOf[AuthorizationCode]
    }
  }

  def authorizationCodeGrant(authorizationCode: String, clientId: String)(implicit ec: ExecutionContext): Future[Option[AuthorizationResult]] = {
    implicit val timeout = AuthorizationService.authorizationCodeGrantTimeout
    val futureResult = actor ? AuthorizationServiceActor.AuthorizationCodeGrant(authorizationCode, clientId)

    futureResult.map { result =>
      result.asInstanceOf[Option[AuthorizationResult]]
    }
  }

  def refreshTokenGrant(refreshToken: String, clientId: String)(implicit ec: ExecutionContext): Future[Option[AuthorizationResult]] = {
    implicit val timeout = AuthorizationService.refreshTokenGrantTimeout
    val futureResult = actor ? AuthorizationServiceActor.RefreshTokenGrant(refreshToken, clientId)

    futureResult.map { result =>
      result.asInstanceOf[Option[AuthorizationResult]]
    }
  }

}
