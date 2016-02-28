package com.persona.service.authorization

import java.security.SecureRandom

object OAuthTokenGenerator {

  private val AlphaNumericRadix = 32
  private val BitsToGenerate = 260

}

class OAuthTokenGenerator(random: SecureRandom) {

  def generate: String = {
    val randomBytes = BigInt(OAuthTokenGenerator.BitsToGenerate, random)

    randomBytes.toString(OAuthTokenGenerator.AlphaNumericRadix)
  }

}
