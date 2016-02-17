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
import com.persona.service.account.{AccountDescriptor, AccountService, SlickAccountDAO}
import com.persona.service.authentication.PersonaAuthService
import com.persona.service.authentication.facebook.FacebookAuthService
import com.persona.service.authentication.google.GoogleAuthService
import com.persona.service.authorization.AuthorizationService
import com.persona.service.bank.{BankService, CassandraBankDAO, DataItemValidator, JsonDataSchemaLoader}
import com.persona.service.chat.ChatService
import com.persona.service.offer.{CassandraOfferDAO, OfferService}
import com.typesafe.config.Config
import slick.jdbc.JdbcBackend._

import scala.concurrent.ExecutionContext

class Bootstrap
  (config: Config, http: HttpExt)
  (implicit actorSystem: ActorSystem, executionContext: ExecutionContext, materializer: Materializer) {

  private[this] val personaConfig = config.getConfig("persona")
  private[this] val googleClientId = personaConfig.getString("google_client_id")

  private[this] val db = Database.forConfig("db", personaConfig)

  private[this] val authorizationService = new AuthorizationService
  private[this] val authorizationApi = new AuthorizationApi(authorizationService)

  private[this] val accountDAO = new SlickAccountDAO(db)
  private[this] val accountService = AccountService(accountDAO)
  private[this] val accountApi = new AccountApi(accountService)

  private[this] val personaAuthService = new PersonaAuthService
  private[this] val facebookAuthService = new FacebookAuthService
  private[this] val googleAuthService = GoogleAuthService(googleClientId, http)
  private[this] val authenticationApi = new AuthenticationApi(
    personaAuthService,
    facebookAuthService,
    googleAuthService
  )

  private[this] val bankDAO = new CassandraBankDAO()
  private[this] val dataSchemaLoader = new JsonDataSchemaLoader(personaConfig.getString("schemaDirectory"))
  private[this] val dataItemValidator = new DataItemValidator(dataSchemaLoader)
  private[this] val bankService = BankService(bankDAO, dataItemValidator)
  private[this] val bankApi = new BankApi(bankService)

  private[this] val offerDAO = new CassandraOfferDAO()
  private[this] val offerService = OfferService(offerDAO)
  private[this] val offerApi = new OfferApi(offerService)

  private[this] val chatService = new ChatService
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
