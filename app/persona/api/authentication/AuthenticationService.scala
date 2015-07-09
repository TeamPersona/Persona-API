package persona.api.authentication

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[OAuthService])
trait AuthenticationService {
  def authenticate
}
