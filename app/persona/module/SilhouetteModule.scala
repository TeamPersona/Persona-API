package persona.module

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.repositories.DelegableAuthInfoRepository
import com.mohiva.play.silhouette.impl.util.{BCryptPasswordHasher, DefaultFingerprintGenerator, PlayCacheLayer, SecureRandomIDGenerator}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import persona.api.authentication.{AuthenticationService, AuthenticationServiceImpl}
import persona.model.authentication.User
import persona.model.dao.authentication.{InMemoryPasswordDAO, InMemoryUserDAOImpl, UserDAO}
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient

class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AuthenticationService].to[AuthenticationServiceImpl]
    bind[UserDAO].to[InMemoryUserDAOImpl]
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[InMemoryPasswordDAO]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideEnvironment(
                          authenticationService: AuthenticationService,
                          authenticatorService: AuthenticatorService[CookieAuthenticator],
                          eventBus: EventBus
                          ): Environment[User, CookieAuthenticator] = {
    Environment[User, CookieAuthenticator](
      authenticationService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  @Provides
  def provideAuthenticatorService(
                                   fingerprintGenerator: FingerprintGenerator,
                                   idGenerator: IDGenerator,
                                   configuration: Configuration,
                                   clock: Clock
                                   ): AuthenticatorService[CookieAuthenticator] = {
    val config = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    new CookieAuthenticatorService(config, None, fingerprintGenerator, idGenerator, clock)
  }

  @Provides
  def provideSocialProviderRegistry(
                                     ): SocialProviderRegistry = {
    SocialProviderRegistry(Seq())
  }

  @Provides
  def provideAuthInfoRepository(
                                 passwordDAO: DelegableAuthInfoDAO[PasswordInfo]
                                 ): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordDAO)
  }

  @Provides
  def provideCredentialsProvider(
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasher: PasswordHasher
                                  ): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))
  }
}
