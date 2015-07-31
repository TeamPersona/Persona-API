package persona.formatter

import com.mohiva.play.silhouette.api.util.Credentials
import play.api.libs.functional.syntax._
import play.api.libs.json._

object CredentialFormat {
  implicit val format = (
    (__ \ "identifier").format[String] ~
      (__ \ "password").format[String])(Credentials.apply, unlift(Credentials.unapply)
    )
}
