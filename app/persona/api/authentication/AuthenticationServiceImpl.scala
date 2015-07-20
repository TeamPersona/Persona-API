package persona.api.authentication

import scala.collection.immutable.HashMap
import scala.concurrent.Future

class AuthenticationServiceImpl extends AuthenticationService {

  /**
   * Mapping from (providerId, userId) to User
   *
   * @todo Static mapping for now
   *       DB support will be added later
   */
  var users: Map[(String, String), User] = HashMap[(String, String), User](
    ("userpass", "test1") -> new User(new BaseProfile("userpass", "test1", Some(Password(null, "123")))),
    ("userpass", "test2") -> new User(new BaseProfile("userpass", "test2", Some(Password(null, "123")))),
    ("userpass", "test3") -> new User(new BaseProfile("userpass", "test3", Some(Password(null, "123"))))
  )

  override def find(providerId: String, userId: String): Future[Option[BaseProfile]] = {
    val result =
      for (
        user <- users.values;
        profile <- user.profiles.find(u => u.providerId == providerId && u.userId == userId)
      ) yield {
        profile
      }
    Future.successful(result.headOption)
  }

  override def getPasswordInfo(user: User): Future[Option[Password]] = {
    Future.successful {
      for (
        found <- users.values.find(u => u.mainProfile.providerId == user.mainProfile.providerId
          && u.mainProfile.userId == user.mainProfile.userId);
        profile <- found.profiles.find(_.providerId == UsernamePasswordProvider.UsernamePasswordId)
      ) yield {
        profile.password.get
      }
    }
  }

  override def updatePasswordInfo(user: User, password: Password): Future[Option[BaseProfile]] = ???
}
