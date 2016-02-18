package com.persona.service.account.google

import com.nimbusds.jwt.{JWTClaimsSet, JWT}
import com.nimbusds.oauth2.sdk.ParseException
import com.persona.service.account.AccountDescriptor

import scalaz.Scalaz._
import scalaz.ValidationNel

class GoogleTokenConverter {

  def convert(idToken: JWT, phoneNumber: String): ValidationNel[GoogleAccountValidationError, GoogleAccountDescriptor] = {
    try
    {
      val claims = idToken.getJWTClaimsSet

      (subject(claims) |@|
       email(claims) |@|
       emailVerified(claims) |@|
       givenName(claims) |@|
       familyName(claims)) { (subject, email, _, givenName, familyName) =>
        GoogleAccountDescriptor(AccountDescriptor(givenName, familyName, email, phoneNumber), subject)
      }
    }
    catch
    {
      case e: ParseException => (new BadClaimsFormatError).failureNel
    }
  }

  private[this] def subject(claims: JWTClaimsSet): ValidationNel[GoogleAccountValidationError, String] = {
    Option(claims.getSubject).map { subject =>
      subject.successNel
    } getOrElse {
      (new MissingSubjectClaimError).failureNel
    }
  }

  private[this] def email(claims: JWTClaimsSet): ValidationNel[GoogleAccountValidationError, String] = {
    Option(claims.getStringClaim("email")).map { email =>
      email.successNel
    } getOrElse {
      (new MissingEmailClaimError).failureNel
    }
  }

  private[this] def emailVerified(claims: JWTClaimsSet): ValidationNel[GoogleAccountValidationError, _] = {
    Option(claims.getBooleanClaim("email_verified")).map { verified =>
      if(verified) {
        verified.successNel
      } else {
        (new EmailNotVerifiedError).failureNel
      }
    } getOrElse {
      (new MissingEmailVerifiedClaimError).failureNel
    }
  }

  private[this] def givenName(claims: JWTClaimsSet): ValidationNel[GoogleAccountValidationError, String] = {
    Option(claims.getStringClaim("given_name")).map { givenName =>
      givenName.successNel
    } getOrElse {
      (new MissingGivenNameClaimError).failureNel
    }
  }

  private[this] def familyName(claims: JWTClaimsSet): ValidationNel[GoogleAccountValidationError, String] = {
    Option(claims.getStringClaim("family_name")).map { familyName =>
      familyName.successNel
    } getOrElse {
      (new MissingFamilyNameClaimError).failureNel
    }
  }

}
