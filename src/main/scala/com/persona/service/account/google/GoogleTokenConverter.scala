package com.persona.service.account.google

import com.nimbusds.jwt.{JWT, JWTClaimsSet}
import com.persona.service.account.AccountDescriptor

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

import scalaz.Scalaz._
import scalaz.ValidationNel

class GoogleTokenConverter {

  def convert(idToken: JWT): ValidationNel[GoogleAccountValidationError, String] = {
    Try(subject(idToken.getJWTClaimsSet)) match {
      case Success(subject) => subject
      case Failure(NonFatal(e)) => (new BadClaimsFormatError).failureNel
    }
  }

  def convert(idToken: JWT, phoneNumber: String): ValidationNel[GoogleAccountValidationError, GoogleAccountDescriptor] = {
    val convertTry = Try {
      val claims = idToken.getJWTClaimsSet
      
      (subject(claims) |@|
        email(claims) |@|
        emailVerified(claims) |@|
        givenName(claims) |@|
        familyName(claims)) { (subject, email, _, givenName, familyName) =>
        GoogleAccountDescriptor(AccountDescriptor(givenName, familyName, email, phoneNumber), subject)
      }
    }

    convertTry match {
      case Success(result) => result
      case Failure(NonFatal(e)) => (new BadClaimsFormatError).failureNel
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
