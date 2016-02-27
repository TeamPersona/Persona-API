package com.persona.service.authorization

import java.security.SecureRandom
import java.util.Base64

object OAuthTokenGenerator {

  private val AlphaNumericRadix = 32
  private val BitsPerCharacter = 5 // log2(32)
  private val BytesToGenerate = AlphaNumericRadix * BitsPerCharacter

}

class OAuthTokenGenerator(random: SecureRandom) {

  def generate: String = {
    val randomBytes = BigInt(OAuthTokenGenerator.BytesToGenerate, random)
    val randomString = randomBytes.toString(OAuthTokenGenerator.AlphaNumericRadix)
    val encodedBytes = Base64.getEncoder.encode(randomString.getBytes)

    encodedBytes.toString
  }

}
