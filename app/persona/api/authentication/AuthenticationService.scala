package persona.api.authentication

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {
  def authenticate
}
