package com.persona.service.authorization

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.persona.service.account.Account
import com.persona.service.account.thirdparty.ThirdPartyAccount
import com.persona.util.actor.ActorWrapper

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AuthorizationServiceActor {

  case class Authorize(account: Account, thirdPartyAccount: ThirdPartyAccount)
  case class GenerateAuthorizationCode(account: Account, thirdPartyAccount: ThirdPartyAccount)
  case class AuthorizationCodeGrant(authorizationCode: String, clientId: String)
  case class RefreshTokenGrant(refreshToken: String, clientId: String)

}

class AuthorizationServiceActor(
  accessTokenGenerator: AccessTokenGenerator,
  accessTokenExpirationTime: Int,
  tokenGenerator: OAuthTokenGenerator,
  refreshTokenDAO: RefreshTokenDAO) extends Actor {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case AuthorizationServiceActor.Authorize(account, thirdPartyAccount) =>
      handleAuthorization(account, thirdPartyAccount, sender)

    case AuthorizationServiceActor.GenerateAuthorizationCode(account, thirdPartyAccount) =>
      // TODO - Differentiate authorization code and access token (For supporting third parties)

    case AuthorizationServiceActor.AuthorizationCodeGrant(authorizationCode, clientId) =>
      // TODO - Generate access token and refresh token (For supporting third parties)

    case AuthorizationServiceActor.RefreshTokenGrant(refreshToken, clientId) =>
      handleRefreshTokenGrant(refreshToken, clientId, sender)
  }

  private[this] def generateAccessToken(account: Account, thirdPartyAccount: ThirdPartyAccount) = {
    // Generator expects expiration time in millis
    // OAuth expects expiration time in seconds
    accessTokenGenerator.generate(account, thirdPartyAccount, accessTokenExpirationTime)
  }

  private[this] def handleAuthorization(account: Account, thirdPartyAccount: ThirdPartyAccount, actor: ActorRef) = {
    val accessToken = generateAccessToken(account, thirdPartyAccount)
    val refreshToken = tokenGenerator.generate
    val refreshTokenDescriptor = RefreshTokenDescriptor(refreshToken, account.id, thirdPartyAccount.id)

    refreshTokenDAO.create(refreshTokenDescriptor).onComplete {
      case Success(_) =>
        actor ! AuthorizationResult(accessToken, accessTokenExpirationTime, Some(refreshToken))

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
  private val generateAuthorizationCodeTimeout = Timeout(60.seconds)
  private val authorizationCodeGrantTimeout = Timeout(60.seconds)
  private val refreshTokenGrantTimeout = Timeout(60.seconds)

  def apply(accessTokenGenerator: AccessTokenGenerator, accessTokenExpirationTime: Int,
            oauthTokenGenerator: OAuthTokenGenerator, refreshTokenDAO: RefreshTokenDAO)
           (implicit actorSystem: ActorSystem): AuthorizationService = {
    val actor = actorSystem.actorOf(
      Props(
        new AuthorizationServiceActor(accessTokenGenerator, accessTokenExpirationTime, oauthTokenGenerator, refreshTokenDAO)
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

  def generateAuthorizationCode(account: Account, thirdPartyAccount: ThirdPartyAccount)(implicit ec: ExecutionContext): Future[String] = {
    implicit val timeout = AuthorizationService.generateAuthorizationCodeTimeout
    val futureResult = actor ? AuthorizationServiceActor.GenerateAuthorizationCode(account, thirdPartyAccount)

    futureResult.map { result =>
      result.asInstanceOf[String]
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
