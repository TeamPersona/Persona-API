package persona.controllers

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.api.{Environment, LoginEvent, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, SocialProviderRegistry}
import persona.api.authentication.AuthenticationService
import persona.formatter.CredentialFormat
import persona.model.authentication.User
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.Action

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Authentication @Inject()(
                                val messagesApi: MessagesApi,
                                val env: Environment[User, CookieAuthenticator],
                                authenticationService: AuthenticationService,
                                authInfoRepository: AuthInfoRepository,
                                credentialsProvider: CredentialsProvider,
                                socialProviderRegistry: SocialProviderRegistry,
                                configuration: Configuration,
                                clock: Clock)
  extends Silhouette[User, CookieAuthenticator] {

  implicit val credentialFormat = CredentialFormat.format

  def authenticate = Action.async(parse.json[Credentials]) { implicit request =>
    credentialsProvider.authenticate(request.body).flatMap { loginInfo =>
      authenticationService.retrieve(loginInfo).flatMap {
        case Some(user) => env.authenticatorService.create(user.loginInfo).flatMap { authenticator =>
          env.eventBus.publish(LoginEvent(user, request, Messages(request2lang, messagesApi)))
          env.authenticatorService.init(authenticator).flatMap { cookie =>
            env.authenticatorService.embed(cookie, Ok("").withCookies(cookie))
          }
        }
        case None =>
          Future.failed(new IdentityNotFoundException("User not found"))
      }
    }
  }

}
