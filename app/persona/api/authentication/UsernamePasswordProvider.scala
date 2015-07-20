package persona.api.authentication

import com.google.common.hash.Hasher
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc.{AnyContent, Controller, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UsernamePasswordProvider(authService: AuthenticationService, hasher: Hasher) extends AuthProvider with Controller {
  override val id: String = UsernamePasswordProvider.UsernamePasswordId

  private def profileFromCredentials(userId: String, password: String): Future[Option[BaseProfile]] = {
    authService.find(id, userId).map {
      maybeUser =>
        for (
          user <- maybeUser;
          pass <- user.password
        // TODO: Check hash information
        ) yield {
          user
        }
    }
  }

  override def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = {
    val form = UsernamePasswordProvider.loginForm.bindFromRequest()
    form.fold(
      errors => Future.successful {
        AuthenticationResult.Failed()
      },
      credentials => {
        val userId = credentials._1.toLowerCase()
        val password = credentials._2

        profileFromCredentials(userId, password).flatMap {
          case Some(profile) => Future.successful {
            AuthenticationResult.Authenticated(profile)
          }
          case None => Future.successful {
            AuthenticationResult.Failed()
          }
        }
      }
    )
  }

  override def authMethod: String = ???
}

object UsernamePasswordProvider {
  val UsernamePasswordId = "userpass"

  val loginForm = Form(
    tuple(
      "username" -> of[String],
      "password" -> of[String]
    )
  )
}