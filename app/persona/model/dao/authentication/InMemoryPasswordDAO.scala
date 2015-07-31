package persona.model.dao.authentication

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.mutable
import scala.concurrent.Future

/**
 * TODO: Cassandra Integration
 */
class InMemoryPasswordDAO extends DelegableAuthInfoDAO[PasswordInfo] {

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    InMemoryPasswordDAO.data -= loginInfo
    Future.successful[Unit]()
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    Future.successful(InMemoryPasswordDAO.data.get(loginInfo))
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    InMemoryPasswordDAO.data += (loginInfo -> authInfo)
    Future.successful(authInfo)
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    InMemoryPasswordDAO.data += (loginInfo -> authInfo)
    Future.successful(authInfo)
  }

}

object InMemoryPasswordDAO {

  /**
   * Static password information mapping
   */
  var data: mutable.HashMap[LoginInfo, PasswordInfo] = mutable.HashMap()
}