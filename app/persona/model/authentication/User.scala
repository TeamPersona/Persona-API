package persona.model.authentication

import java.util.UUID

import com.mohiva.play.silhouette.api.{LoginInfo, Identity}

/**
 * User Model
 *
 * @param userId unique ID of user
 * @param loginInfo login information of user
 * @param name name of user
 * @param email email of user
 */
case class User (
                userId: UUID,
                loginInfo: LoginInfo,
                name: Option[String] = None,
                email: Option[String] = None
                  ) extends Identity