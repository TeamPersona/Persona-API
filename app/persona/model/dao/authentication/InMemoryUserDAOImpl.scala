package persona.model.dao.authentication

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import persona.model.authentication.User

import scala.collection.mutable
import scala.concurrent.Future


class InMemoryUserDAOImpl extends UserDAO {

  override def find(loginInfo: LoginInfo) = Future.successful(
    // FIXME: I thought I wouldn't need class name since this is a companion object
    InMemoryUserDAOImpl.users.find {
      case (id, user) => user.loginInfo == loginInfo
    }.map(_._2)
  )

  override def find(userId: UUID) = Future.successful(InMemoryUserDAOImpl.users.get(userId))

  override def save(user: User) = {
    InMemoryUserDAOImpl.users += (user.userId -> user)
    Future.successful(user)
  }

}

object InMemoryUserDAOImpl {
  /**
   * Static list of users
   */
  def users: mutable.HashMap[UUID, User] = mutable.HashMap()
}