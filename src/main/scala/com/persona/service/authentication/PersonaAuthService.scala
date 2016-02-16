package com.persona.service.authentication

class PersonaAuthService {
  def authenticate(basicAuth: BasicAuth) = {
    s"Hello, ${basicAuth.id}!"
  }
}
