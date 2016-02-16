package com.persona.util.jwk

import java.net.URI

import com.nimbusds.jose.Algorithm
import com.nimbusds.jose.jwk.RSAKey.OtherPrimesInfo
import com.nimbusds.jose.jwk._
import com.nimbusds.jose.util.{Base64, Base64URL}
import spray.json._

import scala.collection.JavaConversions._

sealed trait JWKJsonUtils extends DefaultJsonProtocol {

  // Helper methods

  implicit object KeyOperationJsonFormat extends RootJsonFormat[KeyOperation] {

    def write(keyOperation: KeyOperation): JsValue = JsString(keyOperation.identifier)

    def read(value: JsValue): KeyOperation = value match {
      case JsString(keyOperation) =>
        KeyOperation.values.find { op =>
          keyOperation.equals(op.identifier())
        } getOrElse {
          throw new DeserializationException("Unknown KeyOperation: " + keyOperation)
        }
      case _ => throw new DeserializationException("Invalid KeyOperation")
    }

  }

  implicit object Base64JsonFormat extends RootJsonFormat[Base64] {

    def write(base64: Base64): JsValue = JsString(base64.toString)

    def read(value: JsValue): Base64 = value match {
      case JsString(base64) => new Base64(base64)
      case _ => throw new DeserializationException("Invalid Base64")
    }
  }

  def filterNullElements(elements: JsField*) = {
    elements.filter { element =>
      // Filter out null entries and empty arrays
      element._2 match {
        case JsNull | JsArray.empty => false
        case _ => true
      }
    }
  }

  // Required JWK fields

  def writeKeyType(jwk: JWK): JsField = {
    "kty" -> JsString(jwk.getKeyType.getValue)
  }

  def readKeyType(jwk: JsObject): KeyType = {
    jwk.getFields("kty") match {
      case Seq(JsString(keyType)) => KeyType.parse(keyType)
      case _ => throw new DeserializationException("Invalid jwk kty")
    }
  }

  // Optional JWK fields

  def writeKeyUse(jwk: JWK): JsField = {
    val jsonKeyUse = Option(jwk.getKeyUse).map { keyUse =>
      JsString(keyUse.identifier)
    } getOrElse {
      JsNull
    }

    "use" -> jsonKeyUse
  }

  def readKeyUse(jwk: JsObject): Option[KeyUse] = {
    jwk.getFields("use") match {
      case Seq(JsString(keyUse)) => Some(KeyUse.parse(keyUse))
      case Seq() => None
      case _ => throw new DeserializationException("Invalid jwk use")
    }
  }

  def writeKeyOperations(jwk: JWK): JsField = {
    val jsonKeyOperations = Option(jwk.getKeyOperations).map { keyOperations =>
      keyOperations.toVector.toJson
    } getOrElse {
      JsNull
    }

    "key_ops" -> jsonKeyOperations
  }

  def readKeyOperations(jwk: JsObject): Option[java.util.Set[KeyOperation]] = {
    jwk.getFields("key_ops") match {
      case Seq(keyOps: JsArray) => Some(keyOps.convertTo[Set[KeyOperation]])
      case Seq() => None
      case _ => throw new DeserializationException("Invlalid jwk key_ops")
    }
  }

  def writeAlgorithm(jwk: JWK): JsField = {
    val jsonAlgorithm = Option(jwk.getAlgorithm).map { algorithm =>
      JsString(algorithm.getName)
    } getOrElse {
      JsNull
    }

    "alg" -> jsonAlgorithm
  }

  def readAlgorithm(jwk: JsObject): Option[Algorithm] = {
    jwk.getFields("alg") match {
      case Seq(JsString(algorithm)) => Some(new Algorithm(algorithm))
      case Seq() => None
      case _ => throw new DeserializationException("Invalid jwk alg")
    }
  }

  def writeKeyId(jwk: JWK): JsField = {
    val jsonKeyId = Option(jwk.getKeyID).map { keyId =>
      JsString(keyId)
    } getOrElse {
      JsNull
    }

    "kid" -> jsonKeyId
  }

  def readKeyId(jwk: JsObject): Option[String] = {
    jwk.getFields("kid") match {
      case Seq(JsString(keyId)) => Some(keyId)
      case Seq() => None
      case _ => throw new DeserializationException("Invalid jwk kid")
    }
  }

  def writeX509CertUrl(jwk: JWK): JsField = {
    val jsonX509CertUrl = Option(jwk.getX509CertURL).map { X509CertUrl =>
      JsString(X509CertUrl.toString)
    } getOrElse {
      JsNull
    }

    "x5u" -> jsonX509CertUrl
  }

  def readX509CertUrl(jwk: JsObject): Option[URI] = {
    jwk.getFields("x5u") match {
      case Seq(JsString(x509CertUrl)) => Some(new URI(x509CertUrl))
      case Seq() => None
      case _ => throw new DeserializationException("Invalid jwk x5u")
    }
  }

  def writeX509CertThumbprint(jwk: JWK): JsField = {
    val jsonX509CertThumbprint = Option(jwk.getX509CertThumbprint).map { X509CertThumbprint =>
      JsString(X509CertThumbprint.toString)
    } getOrElse {
      JsNull
    }

    "x5t" -> jsonX509CertThumbprint
  }

  def readX509CertThumbprint(jwk: JsObject): Option[Base64URL] = {
    jwk.getFields("x5t") match {
      case Seq(JsString(x509CertThumbprint)) => Some(new Base64URL(x509CertThumbprint))
      case Seq() => None
      case _ => throw new DeserializationException("Invalid jwk x5t")
    }
  }

  def writeX509CertChain(jwk: JWK): JsField = {
    val jsonX509CertChain = Option(jwk.getX509CertChain).map { X509CertChain =>
      X509CertChain.toVector.toJson
    } getOrElse {
      JsNull
    }

    "x5c" -> jsonX509CertChain
  }

  def readX509CertChain(jwk: JsObject): Option[java.util.List[Base64]] = {
    jwk.getFields("x5c") match {
      case Seq(x509Certs: JsArray) => Some(x509Certs.convertTo[List[Base64]])
      case Seq() => None
      case _ => throw new DeserializationException("Invalid jwk x5c")
    }
  }

}

trait JwkJsonProtocol extends DefaultJsonProtocol {

  implicit object ECKeyJsonFormat extends RootJsonFormat[ECKey] with JWKJsonUtils {

    // Required ECKey fields

    def writeCurve(ecKey: ECKey): JsField = {
      "crv" -> JsString(ecKey.getCurve.toString)
    }

    def readCurve(ecKey: JsObject): ECKey.Curve = {
      ecKey.getFields("crv") match {
        case Seq(JsString(curve)) => ECKey.Curve.parse(curve)
        case _ => throw new DeserializationException("Invalid eckey crv")
      }
    }

    def writeXCoordinate(ecKey: ECKey): JsField = {
      "x" -> JsString(ecKey.getX.toString)
    }

    def readXCoordinate(ecKey: JsObject): Base64URL = {
      ecKey.getFields("x") match {
        case Seq(JsString(x)) => new Base64URL(x)
        case _ => throw new DeserializationException("Invalid eckey x")
      }
    }

    def writeYCoordinate(ecKey: ECKey): JsField = {
      "y" -> JsString(ecKey.getY.toString)
    }

    def readYCoordinate(ecKey: JsObject): Base64URL = {
      ecKey.getFields("y") match {
        case Seq(JsString(y)) => new Base64URL(y)
        case _ => throw new DeserializationException("Invalid eckey y")
      }
    }

    // Optional ECKey fields

    def writeDCoordinate(ecKey: ECKey): JsField = {
      val jsonDCoordinate = Option(ecKey.getD).map { dCoordinate =>
        JsString(dCoordinate.toString)
      } getOrElse {
        JsNull
      }

      "d" -> jsonDCoordinate
    }

    def readDCoordinate(ecKey: JsObject): Option[Base64URL] = {
      ecKey.getFields("d") match {
        case Seq(JsString(d)) => Some(new Base64URL(d))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid eckey d")
      }
    }

    def write(ecKey: ECKey): JsValue = {
      val elements = filterNullElements(writeKeyType(ecKey),
                                        writeKeyUse(ecKey),
                                        writeKeyOperations(ecKey),
                                        writeAlgorithm(ecKey),
                                        writeKeyId(ecKey),
                                        writeX509CertUrl(ecKey),
                                        writeX509CertThumbprint(ecKey),
                                        writeX509CertChain(ecKey),
                                        writeCurve(ecKey),
                                        writeXCoordinate(ecKey),
                                        writeYCoordinate(ecKey),
                                        writeDCoordinate(ecKey))

      JsObject(Map(elements: _*))
    }

    def read(ecKey: JsValue): ECKey = {
      val ecKeyAsJsObject = ecKey.asJsObject
      val kty = readKeyType(ecKeyAsJsObject)

      if(KeyType.EC != kty) {
        throw new DeserializationException("ECKey trying to parse JWK of type " + kty)
      }

      val crv = readCurve(ecKeyAsJsObject)
      val x = readXCoordinate(ecKeyAsJsObject)
      val y = readYCoordinate(ecKeyAsJsObject)
      val optionalD = readDCoordinate(ecKeyAsJsObject)
      val optionalUse = readKeyUse(ecKeyAsJsObject)
      val optionalKeyOps = readKeyOperations(ecKeyAsJsObject)
      val optionalAlg = readAlgorithm(ecKeyAsJsObject)
      val optionalKid = readKeyId(ecKeyAsJsObject)
      val optionalX5u = readX509CertUrl(ecKeyAsJsObject)
      val optionalX5t = readX509CertThumbprint(ecKeyAsJsObject)
      val optionalX5c = readX509CertChain(ecKeyAsJsObject)

      optionalD.map { d =>
        new ECKey(crv, x, y, d,
                  optionalUse.orNull,
                  optionalKeyOps.orNull,
                  optionalAlg.orNull,
                  optionalKid.orNull,
                  optionalX5u.orNull,
                  optionalX5t.orNull,
                  optionalX5c.orNull)
      } getOrElse {
        new ECKey(crv, x, y,
                  optionalUse.orNull,
                  optionalKeyOps.orNull,
                  optionalAlg.orNull,
                  optionalKid.orNull,
                  optionalX5u.orNull,
                  optionalX5t.orNull,
                  optionalX5c.orNull)
      }
    }

  }

  implicit object OtherPrimesInfoJsonFormat extends RootJsonFormat[OtherPrimesInfo] {

    def write(otherPrimesInfo: OtherPrimesInfo): JsValue = {
      JsObject(
        "r" -> JsString(otherPrimesInfo.getPrimeFactor.toString),
        "d" -> JsString(otherPrimesInfo.getFactorCRTExponent.toString),
        "t" -> JsString(otherPrimesInfo.getFactorCRTCoefficient.toString)
      )
    }

    def read(otherPrimesInfo: JsValue): OtherPrimesInfo = {
      otherPrimesInfo.asJsObject.getFields("r", "d", "t") match {
        case Seq(JsString(jsonR), JsString(jsonD), JsString(jsonT)) =>
          val r = new Base64URL(jsonR)
          val d = new Base64URL(jsonD)
          val t = new Base64URL(jsonT)

          new OtherPrimesInfo(r, d, t)
        case _ => throw new DeserializationException("Invalid OtherPrimesInfo")
      }
    }

  }

  implicit object RSAKeyJsonFormat extends RootJsonFormat[RSAKey] with JWKJsonUtils {

    // Required RSAKey fields

    def writeModulus(rsaKey: RSAKey): JsField = {
      "n" -> JsString(rsaKey.getModulus.toString)
    }

    def readModulus(rsaKey: JsObject): Base64URL = {
      rsaKey.getFields("n") match {
        case Seq(JsString(n)) => new Base64URL(n)
        case _ => throw new DeserializationException("Invalid rsakey n")
      }
    }

    def writePublicExponent(rsaKey: RSAKey): JsField = {
      "e" -> JsString(rsaKey.getPublicExponent.toString)
    }

    def readPublicExponent(rsaKey: JsObject): Base64URL = {
      rsaKey.getFields("e") match {
        case Seq(JsString(e)) => new Base64URL(e)
        case _ => throw new DeserializationException("Invalid rsakey e")
      }
    }

    // Optional RSAKey fields
    // First possible RSAKey representation

    def writePrivateExponent(rsaKey: RSAKey): JsField = {
      val jsonPrivateExponent = Option(rsaKey.getPrivateExponent).map { privateExponent =>
        JsString(privateExponent.toString)
      } getOrElse {
        JsNull
      }

      "d" -> jsonPrivateExponent
    }

    def readPrivateExponent(rsaKey: JsObject): Option[Base64URL] = {
      rsaKey.getFields("d") match {
        case Seq(JsString(d)) => Some(new Base64URL(d))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey d")
      }
    }

    // Second possible RSAKey representation

    def writeFirstPrimeFactor(rsaKey: RSAKey): JsField = {
      val jsonFirstPrimeFactor = Option(rsaKey.getFirstPrimeFactor).map { firstPrimeFactor =>
        JsString(firstPrimeFactor.toString)
      } getOrElse {
        JsNull
      }

      "p" -> jsonFirstPrimeFactor
    }

    def readFirstPrimeFactor(rsaKey: JsObject): Option[Base64URL] = {
      rsaKey.getFields("p") match {
        case Seq(JsString(p)) => Some(new Base64URL(p))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey p")
      }
    }

    def writeSecondPrimeFactor(rsaKey: RSAKey): JsField = {
      val jsonSecondPrimeFactor = Option(rsaKey.getSecondPrimeFactor).map { secondPrimeFactor =>
        JsString(secondPrimeFactor.toString)
      } getOrElse {
        JsNull
      }

      "q" -> jsonSecondPrimeFactor
    }

    def readSecondPrimeFactor(rsaKey: JsObject): Option[Base64URL] = {
      rsaKey.getFields("q") match {
        case Seq(JsString(q)) => Some(new Base64URL(q))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey q")
      }
    }

    def writeFirstFactorCRTExponent(rsaKey: RSAKey): JsField = {
      val jsonFirstFactorCRTExponent = Option(rsaKey.getFirstFactorCRTExponent).map { firstFactorCRTExponent =>
        JsString(firstFactorCRTExponent.toString)
      } getOrElse {
        JsNull
      }

      "dp" -> jsonFirstFactorCRTExponent
    }

    def readFirstFactorCRTExponent(rsaKey: JsObject): Option[Base64URL] = {
      rsaKey.getFields("dp") match {
        case Seq(JsString(dp)) => Some(new Base64URL(dp))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey dp")
      }
    }

    def writeSecondFactorCRTExponent(rsaKey: RSAKey): JsField = {
      val jsonSecondFactorCRTExponent = Option(rsaKey.getSecondFactorCRTExponent).map { secondFactorCRTExponent =>
        JsString(secondFactorCRTExponent.toString)
      } getOrElse {
        JsNull
      }

      "dq" -> jsonSecondFactorCRTExponent
    }

    def readSecondFactorCRTExponent(rsaKey: JsObject): Option[Base64URL] = {
      rsaKey.getFields("dq") match {
        case Seq(JsString(dq)) => Some(new Base64URL(dq))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey dq")
      }
    }

    def writeFirstCRTCoefficient(rsaKey: RSAKey): JsField = {
      val jsonFirstCRTCoefficient = Option(rsaKey.getFirstCRTCoefficient).map { firstCRTCoefficient =>
        JsString(firstCRTCoefficient.toString)
      } getOrElse {
        JsNull
      }

      "qi" -> jsonFirstCRTCoefficient
    }

    def readFirstCRTCoefficient(rsaKey: JsObject): Option[Base64URL] = {
      rsaKey.getFields("qi") match {
        case Seq(JsString(qi)) => Some(new Base64URL(qi))
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey qi")
      }
    }

    def writeOtherPrimes(rsaKey: RSAKey): JsField = {
      val jsonOth = Option(rsaKey.getOtherPrimes).map { otherPrimes =>
        otherPrimes.toVector.toJson
      } getOrElse {
        JsNull
      }

      "oth" -> jsonOth
    }

    def readOtherPrimes(rsaKey: JsObject): Option[java.util.List[OtherPrimesInfo]] = {
      rsaKey.getFields("oth") match {
        case Seq(otherPrimes: JsArray) => Some(otherPrimes.convertTo[List[OtherPrimesInfo]])
        case Seq() => None
        case _ => throw new DeserializationException("Invalid rsakey oth")
      }
    }

    def write(rsaKey: RSAKey): JsValue = {
      val elements = filterNullElements(writeKeyType(rsaKey),
                                        writeKeyUse(rsaKey),
                                        writeKeyOperations(rsaKey),
                                        writeAlgorithm(rsaKey),
                                        writeKeyId(rsaKey),
                                        writeX509CertUrl(rsaKey),
                                        writeX509CertThumbprint(rsaKey),
                                        writeX509CertChain(rsaKey),
                                        writeModulus(rsaKey),
                                        writePublicExponent(rsaKey),
                                        writePrivateExponent(rsaKey),
                                        writeFirstPrimeFactor(rsaKey),
                                        writeSecondPrimeFactor(rsaKey),
                                        writeFirstFactorCRTExponent(rsaKey),
                                        writeSecondFactorCRTExponent(rsaKey),
                                        writeFirstCRTCoefficient(rsaKey),
                                        writeOtherPrimes(rsaKey))

      JsObject(Map(elements: _*))
    }

    def read(rsaKey: JsValue): RSAKey = {
      val rsaKeyAsJsObject = rsaKey.asJsObject
      val kty = readKeyType(rsaKeyAsJsObject)

      if(KeyType.RSA != kty) {
        throw new DeserializationException("RSAKey trying to parse JWK of type " + kty)
      }

      val n = readModulus(rsaKeyAsJsObject)
      val e = readPublicExponent(rsaKeyAsJsObject)
      val optionalD = readPrivateExponent(rsaKeyAsJsObject)
      val optionalP = readFirstPrimeFactor(rsaKeyAsJsObject)
      val optionalQ = readSecondPrimeFactor(rsaKeyAsJsObject)
      val optionalDp = readFirstFactorCRTExponent(rsaKeyAsJsObject)
      val optionalDq = readSecondFactorCRTExponent(rsaKeyAsJsObject)
      val optionalQi = readFirstCRTCoefficient(rsaKeyAsJsObject)
      val optionalOth = readOtherPrimes(rsaKeyAsJsObject)
      val optionalUse = readKeyUse(rsaKeyAsJsObject)
      val optionalKeyOps = readKeyOperations(rsaKeyAsJsObject)
      val optionalAlg = readAlgorithm(rsaKeyAsJsObject)
      val optionalKid = readKeyId(rsaKeyAsJsObject)
      val optionalX5u = readX509CertUrl(rsaKeyAsJsObject)
      val optionalX5t = readX509CertThumbprint(rsaKeyAsJsObject)
      val optionalX5c = readX509CertChain(rsaKeyAsJsObject)

      new RSAKey(n, e,
                 optionalD.orNull,
                 optionalP.orNull,
                 optionalQ.orNull,
                 optionalDp.orNull,
                 optionalDq.orNull,
                 optionalQi.orNull,
                 optionalOth.orNull,
                 optionalUse.orNull,
                 optionalKeyOps.orNull,
                 optionalAlg.orNull,
                 optionalKid.orNull,
                 optionalX5u.orNull,
                 optionalX5t.orNull,
                 optionalX5c.orNull)
    }

  }

  implicit object JWKJsonFormat extends RootJsonFormat[JWK] with JWKJsonUtils {

    def write(jwk: JWK): JsValue = jwk match {
      case ecKey: ECKey => ecKey.toJson
      case rsaKey: RSAKey => rsaKey.toJson
    }

    def read(jwk: JsValue): JWK = readKeyType(jwk.asJsObject) match {
      case KeyType.EC => jwk.convertTo[ECKey]
      case KeyType.RSA => jwk.convertTo[RSAKey]
    }

  }

  implicit object JWKSetJsonFormat extends RootJsonFormat[JWKSet] with DefaultJsonProtocol {

    def write(jwkSet: JWKSet): JsValue = JsObject("keys" -> jwkSet.getKeys.toVector.toJson)

    def read(jwkSet: JsValue): JWKSet = {
      jwkSet.asJsObject.getFields("keys") match {
        case Seq(jwks: JsArray) => new JWKSet(jwks.convertTo[List[JWK]])
        case _ => throw new DeserializationException("Invalid JWKSet")
      }
    }

  }

}
