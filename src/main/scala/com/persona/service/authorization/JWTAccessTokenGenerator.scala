package com.persona.service.authorization

import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jose.crypto.{ECDSAVerifier, ECDSASigner}

import java.security.interfaces.{ECPrivateKey, ECPublicKey}

import com.nimbusds.jwt.{SignedJWT, JWTClaimsSet}
import com.persona.service.account.Account
import com.persona.service.account.thirdparty.ThirdPartyAccount
import org.joda.time.DateTime

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

class JWTAccessTokenGenerator(
  publicKey: ECPublicKey,
  privateKey: ECPrivateKey,
  issuer: String)
  extends AccessTokenGenerator {

  private[this] val signer = new ECDSASigner(privateKey)
  private[this] val verifier = new ECDSAVerifier(publicKey)

  def generate(account: Account, thirdPartyAccount: ThirdPartyAccount, expirationTime: Int): String = {
    val currentTime = DateTime.now
    val claimsSet = new JWTClaimsSet.Builder().subject(account.id.toString)
                                              .audience(thirdPartyAccount.id)
                                              .issuer(issuer)
                                              .issueTime(currentTime.toDate)
                                              .expirationTime(currentTime.plus(expirationTime).toDate)
                                              .build

    val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.ES256), claimsSet)
    jwt.sign(signer)
    jwt.serialize
  }

  def verify(accessToken: String): Option[(Int, String)] = {
    Try(SignedJWT.parse(accessToken)) match {
      case Success(jwt) =>
        Try(jwt.verify(verifier)) match {
          case Success(verified) =>
            if(verified) {
              val claims = jwt.getJWTClaimsSet
              val audience = claims.getAudience.toList

              if(1 == audience.size) {
                Option(claims.getSubject).flatMap { accountIdString =>
                  Try(accountIdString.toInt) match {
                    case Success(accountId) =>
                      Some((accountId, audience.head))

                    case Failure(e) =>
                      None
                  }
                }
              } else {
                None
              }
            } else {
              None
            }

          case Failure(e) =>
            None
        }

      case Failure(e) =>
        None
    }
  }

}
