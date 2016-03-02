package com.persona.service.authorization

import java.security.SecureRandom

object SecureAlphanumericStringGenerator {

  private val AlphaNumericRadix = 32
  private val BitsToGenerate = 260

}

class SecureAlphanumericStringGenerator(random: SecureRandom) {

  def generate: String = {
    val randomBytes = BigInt(SecureAlphanumericStringGenerator.BitsToGenerate, random)

    randomBytes.toString(SecureAlphanumericStringGenerator.AlphaNumericRadix)
  }

}
