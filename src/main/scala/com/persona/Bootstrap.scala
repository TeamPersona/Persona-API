package com.persona

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import com.persona.http.account.AccountApi
import com.persona.http.authentication.AuthenticationApi
import com.persona.http.authorization.AuthorizationApi
import com.persona.http.bank.BankApi
import com.persona.http.chat.ChatApi
import com.persona.http.offer.OfferApi
import com.persona.service.account.google.{GoogleAccountService, GoogleTokenConverter, SlickGoogleAccountDAO}
import com.persona.service.account.thirdparty.SlickThirdPartyAccountDAO
import com.persona.service.account.{AccountService, AccountValidator, SlickAccountDAO}
import com.persona.service.authentication.AuthenticationService
import com.persona.service.authentication.google.{GoogleAuthenticationService, GoogleTokenValidationService}
import com.persona.service.authorization._
import com.persona.service.bank._
import com.persona.service.chat.ChatService
import com.persona.service.offer.{PostgresOfferDataDAO, OfferService}
import com.persona.util.security.SecureAlphanumericStringGenerator

import com.typesafe.config.Config

import java.security.interfaces.{ECPrivateKey, ECPublicKey}
import java.security.{KeyPairGenerator, SecureRandom}

import scala.concurrent.ExecutionContext

import slick.jdbc.JdbcBackend._

object Bootstrap {

  private val ECKeySize = 256

}

class Bootstrap
  (config: Config, http: HttpExt)
  (implicit actorSystem: ActorSystem, executionContext: ExecutionContext, materializer: Materializer) {

  private[this] val personaConfig = config.getConfig("persona")

  private[this] val db = Database.forConfig("db", personaConfig)

  private[this] val secureRandom = SecureRandom.getInstanceStrong
  private[this] val stringGenerator = new SecureAlphanumericStringGenerator(secureRandom)

  private[this] val keyGenerator = KeyPairGenerator.getInstance("EC")
  keyGenerator.initialize(Bootstrap.ECKeySize, secureRandom)
  private[this] val keyPair = keyGenerator.generateKeyPair

  private[this] val accountValidator = new AccountValidator
  private[this] val passwordLogRounds = personaConfig.getInt("passwordLogRounds")
  private[this] val accountDAO = new SlickAccountDAO(db)
  private[this] val thirdPartyAccountDAO = new SlickThirdPartyAccountDAO(db, secureRandom, stringGenerator)
  private[this] val accountService = AccountService(accountDAO, thirdPartyAccountDAO, passwordLogRounds)
  private[this] val googleTokenConverter = new GoogleTokenConverter
  private[this] val googleAccountDAO = new SlickGoogleAccountDAO(db)
  private[this] val googleClientId = personaConfig.getString("google_client_id")
  private[this] val googleTokenValidationService = GoogleTokenValidationService(googleClientId, http)
  private[this] val googleAccountService = GoogleAccountService(
    googleTokenConverter,
    googleAccountDAO,
    googleTokenValidationService
  )

  private[this] val publicKey = keyPair.getPublic.asInstanceOf[ECPublicKey]
  private[this] val privateKey = keyPair.getPrivate.asInstanceOf[ECPrivateKey]
  private[this] val issuer = personaConfig.getString("jwt_issuer")
  private[this] val accessTokenGenerator = new JWTAccessTokenGenerator(publicKey, privateKey, issuer)
  private[this] val accessTokenExpirationTime = personaConfig.getInt("oauth_expiration_time")
  private[this] val authorizationCodeDAO = new SlickAuthorizationCodeDAO(db)
  private[this] val refreshTokenDAO = new SlickRefreshTokenDAO(db)
  private[this] val authorizationService = AuthorizationService(
    accountService,
    accessTokenGenerator,
    accessTokenExpirationTime,
    stringGenerator,
    authorizationCodeDAO,
    refreshTokenDAO
  )

  private[this] val authenticationService = AuthenticationService(accountDAO)
  private[this] val googleAuthenticationService = GoogleAuthenticationService(
    googleTokenConverter,
    googleAccountDAO,
    googleTokenValidationService
  )

  private[this] val dataItemsDAO = new DataItemsDAO
  private[this] val dataCountsDAO = new DataCountsDAO
  private[this] val bankDAO = new CassandraBankDAO(dataItemsDAO, dataCountsDAO)
  private[this] val dataSchemaLoader = new JsonDataSchemaLoader(personaConfig.getString("schemaDirectory"))
  private[this] val dataSchemaManager = new DataSchemaManager(dataSchemaLoader)
  private[this] val dataItemValidator = new DataItemValidator(dataSchemaManager)
  private[this] val bankService = BankService(bankDAO, dataItemValidator, accountService, dataSchemaManager)

  private[this] val offerDAO = new PostgresOfferDataDAO(db, bankDAO)
  private[this] val offerService = OfferService(offerDAO)

  private[this] val chatService = new ChatService

  private[this] val accountApi = new AccountApi(accountService, accountValidator, googleAccountService, authorizationService)
  private[this] val authenticationApi = new AuthenticationApi(authenticationService, googleAuthenticationService, accountService, authorizationService)
  private[this] val authorizationApi = new AuthorizationApi(authorizationService)
  private[this] val bankApi = new BankApi(bankService, authorizationService)
  private[this] val offerApi = new OfferApi(offerService, authorizationService)
  private[this] val chatApi = new ChatApi(chatService)

  val routes = {
    accountApi.route ~
    authenticationApi.route ~
    authorizationApi.route ~
    bankApi.route ~
    offerApi.route ~
    chatApi.route
  }

}
