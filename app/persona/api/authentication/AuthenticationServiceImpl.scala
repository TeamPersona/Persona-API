package persona.api.authentication


import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import persona.model.authentication.User
import persona.model.dao.authentication.UserDAO
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class AuthenticationServiceImpl @Inject()(userDAO: UserDAO) extends AuthenticationService {

  override def save(user: User): Future[User] = userDAO.save(user)

  override def save(profile: CommonSocialProfile): Future[User] = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) =>
        userDAO.save(user.copy(
          name = profile.fullName,
          email = profile.email
        ))
      case None =>
        userDAO.save(User(
          userId = UUID.randomUUID(),
          loginInfo = profile.loginInfo,
          name = profile.fullName,
          email = profile.email
        ))
    }
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

}