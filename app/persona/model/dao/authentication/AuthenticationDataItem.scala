package persona.model.dao.authentication

import java.util.UUID

case class AuthenticationDataItem (
                                    userId: UUID,
                                    name: Option[String],
                                    email: Option[String]
                                    )
