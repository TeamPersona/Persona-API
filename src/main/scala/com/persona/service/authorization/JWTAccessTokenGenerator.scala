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
              validateJWT(jwt)
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

  private[this] def validateJWT(jwt: SignedJWT) = {
    Try(jwt.getJWTClaimsSet) match {
      case Success(claims) =>
        validateClaims(claims)

      case Failure(e) =>
        None
    }
  }

  private[this] def validateClaims(claims: JWTClaimsSet) = {
    Option(claims.getIssuer).flatMap { jwtIssuer =>
      if(jwtIssuer == issuer) {
        Option(claims.getExpirationTime).flatMap { expirationTime =>
          val jwtExpirationTime = new DateTime(expirationTime)

          if(jwtExpirationTime.isAfter(DateTime.now)) {
            Option(claims.getAudience).flatMap { audience =>
              val jwtAudience = audience.toList

              if(1 == jwtAudience.size) {
                val thirdPartyAccountId = jwtAudience.head

                Option(claims.getSubject).flatMap { jwtSubject =>
                  Try(jwtSubject.toInt) match {
                    case Success(accountId) =>
                      Some((accountId, thirdPartyAccountId))

                    case Failure(e) =>
                      None
                  }
                }
              } else {
                None
              }
            }
          } else {
            None
          }
        }
      } else {
        None
      }
    }
  }

}
