package com.persona.util.jwk

import java.net.URI

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.RSAKey.OtherPrimesInfo
import com.nimbusds.jose.jwk._
import com.nimbusds.jose.util.{Base64, Base64URL}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

import scala.collection.JavaConversions._

class JwkJsonProtocolTest extends WordSpec with Matchers with JwkJsonProtocol {

  "JwkJsonProtocol" should {
    "parse a jwk of type rsa" in {
      val json =
        """
        {
          "kty" : "RSA",
          "n"   : "n",
          "e"   : "e"
        }
        """.parseJson

      val jwk = json.convertTo[JWK]

      jwk.getKeyType shouldBe KeyType.RSA
      jwk shouldBe a[RSAKey]
    }

    "write a jwk of type rsa" in {
      val jwk = new RSAKey.Builder(new Base64URL("n"),
                                   new Base64URL("e")
                                  )
                                  .build

      val json = jwk.toJson.asJsObject

      val kty = json.getFields("kty")
      kty should have length 1
      kty should contain theSameElementsAs Seq(JsString("RSA"))
    }

    "parse a jwk of type ec" in {
      val json =
        """
        {
          "kty" : "EC",
          "crv" : "P-256",
          "x"   : "x",
          "y"   : "y"
        }
        """.parseJson

      val jwk = json.convertTo[JWK]

      jwk.getKeyType shouldBe KeyType.EC
      jwk shouldBe an[ECKey]
    }

    "write a jwk of type ec" in {
      val jwk = new ECKey.Builder(ECKey.Curve.P_256,
                                  new Base64URL("x"),
                                  new Base64URL("y")
                                 )
                                 .build

      val json = jwk.toJson.asJsObject

      val kty = json.getFields("kty")
      kty should have length 1
      kty should contain theSameElementsAs Seq(JsString("EC"))
    }

    // KeyOperations and KeyUse cannot be set at the same time
    // Make one test for each
    "parse a jwk with KeyUse" in {
      val json =
        """
        {
          "kty" : "EC",
          "use" : "enc",
          "crv" : "P-256",
          "x"   : "x",
          "y"   : "y"
        }
        """.parseJson

      val jwk = json.convertTo[JWK]

      jwk.getKeyUse shouldBe KeyUse.ENCRYPTION
    }

    "write a jwk with KeyUse" in {
      val ecKey = new ECKey.Builder(ECKey.Curve.P_256,
                                    new Base64URL("x"),
                                    new Base64URL("y")
                                   )
                                   .keyUse(KeyUse.SIGNATURE)
                                   .build

      val json = ecKey.toJson.asJsObject

      val use = json.getFields("use")
      use should have length 1
      use should contain theSameElementsAs Seq(JsString(KeyUse.SIGNATURE.toString))
    }

    "parse a jwk with all optional parameters except KeyUse" in {
      val json =
        """
        {
          "kty" : "EC",
          "key_ops" : [
            "encrypt",
            "decrypt"
          ],
          "alg" : "RS256",
          "kid" : "KeyId",
          "x5u" : "http://www.test.com",
          "x5t" : "thumbprint",
          "x5c" : [
            "chain1",
            "chain2"
          ],
          "crv" : "P-256",
          "x"   : "x",
          "y"   : "y"
        }
        """.parseJson

      val jwk = json.convertTo[JWK]

      jwk.getKeyOperations should contain theSameElementsAs List(KeyOperation.ENCRYPT, KeyOperation.DECRYPT)
      jwk.getAlgorithm shouldBe JWSAlgorithm.RS256
      jwk.getKeyID shouldBe "KeyId"
      jwk.getX509CertURL should be(new URI("http://www.test.com"))
      jwk.getX509CertThumbprint should be(new Base64URL("thumbprint"))
      jwk.getX509CertChain should contain theSameElementsAs List(new Base64("chain1"), new Base64("chain2"))
    }

    "write a jwk with all optional parameters except KeyUse" in {
      val ecKey = new ECKey.Builder(ECKey.Curve.P_256,
                                    new Base64URL("x"),
                                    new Base64URL("y")
                                   )
                                   .keyOperations(Set(KeyOperation.ENCRYPT, KeyOperation.DECRYPT))
                                   .algorithm(JWSAlgorithm.RS256)
                                   .keyID("KeyId")
                                   .x509CertURL(new URI("http://www.test.com"))
                                   .x509CertThumbprint(new Base64URL("thumbprint"))
                                   .x509CertChain(List(new Base64("chain1"), new Base64("chain2")))
                                   .build

      val json = ecKey.toJson.asJsObject

      val keyOps = json.getFields("key_ops")
      keyOps should have length 1
      keyOps should contain theSameElementsAs Seq(JsArray(JsString("encrypt"), JsString("decrypt")))

      val alg = json.getFields("alg")
      alg should have length 1
      alg should contain theSameElementsAs Seq(JsString("RS256"))

      val kid = json.getFields("kid")
      kid should have length 1
      kid should contain theSameElementsAs Seq(JsString("KeyId"))

      val x5u = json.getFields("x5u")
      x5u should have length 1
      x5u should contain theSameElementsAs Seq(JsString("http://www.test.com"))

      val x5t = json.getFields("x5t")
      x5t should have length 1
      x5t should contain theSameElementsAs Seq(JsString("thumbprint"))

      val x5c = json.getFields("x5c")
      x5c should have length 1
      x5c should contain theSameElementsAs Seq(JsArray(JsString("chain1"), JsString("chain2")))
    }

    "parse a public RSAKey" in {
      val json =
        """
        {
          "kty" : "RSA",
          "n"   : "n",
          "e"   : "e"
        }
        """.parseJson

      val rsaKey = json.convertTo[RSAKey]

      rsaKey.getModulus shouldBe new Base64URL("n")
      rsaKey.getPublicExponent shouldBe new Base64URL("e")
    }

    "parse a private first representation RSAKey" in {
      val json =
        """
        {
          "kty" : "RSA",
          "n"   : "n",
          "e"   : "e",
          "d"   : "d"
        }
        """.parseJson

      val rsaKey = json.convertTo[RSAKey]

      rsaKey.getModulus shouldBe new Base64URL("n")
      rsaKey.getPublicExponent shouldBe new Base64URL("e")
      rsaKey.getPrivateExponent shouldBe new Base64URL("d")
    }

    "parse a private second representation RSAKey" in {
      val json =
        """
        {
          "kty" : "RSA",
          "n"   : "n",
          "e"   : "e",
          "p"   : "p",
          "q"   : "q",
          "dp"  : "dp",
          "dq"  : "dq",
          "qi"  : "qi",
          "oth" : [
             {
               "r" : "r",
               "d" : "d",
               "t" : "t"
             }
           ]
        }
        """.parseJson

      val rsaKey = json.convertTo[RSAKey]

      rsaKey.getModulus shouldBe new Base64URL("n")
      rsaKey.getPublicExponent shouldBe new Base64URL("e")
      rsaKey.getFirstPrimeFactor shouldBe new Base64URL("p")
      rsaKey.getSecondPrimeFactor shouldBe new Base64URL("q")
      rsaKey.getFirstFactorCRTExponent shouldBe new Base64URL("dp")
      rsaKey.getSecondFactorCRTExponent shouldBe new Base64URL("dq")
      rsaKey.getFirstCRTCoefficient shouldBe new Base64URL("qi")
      rsaKey.getOtherPrimes should have size 1

      val otherPrime = rsaKey.getOtherPrimes.get(0)

      otherPrime.getPrimeFactor shouldBe new Base64URL("r")
      otherPrime.getFactorCRTExponent shouldBe new Base64URL("d")
      otherPrime.getFactorCRTCoefficient shouldBe new Base64URL("t")
    }

    "parse a private first and second representation RSAKey" in {
      val json =
        """
        {
          "kty" : "RSA",
          "n"   : "n",
          "e"   : "e",
          "d"   : "d",
          "p"   : "p",
          "q"   : "q",
          "dp"  : "dp",
          "dq"  : "dq",
          "qi"  : "qi",
          "oth" : [
             {
               "r" : "r",
               "d" : "d",
               "t" : "t"
             }
           ]
        }
        """.parseJson

      val rsaKey = json.convertTo[RSAKey]

      rsaKey.getModulus shouldBe new Base64URL("n")
      rsaKey.getPublicExponent shouldBe new Base64URL("e")
      rsaKey.getPrivateExponent shouldBe new Base64URL("d")
      rsaKey.getFirstPrimeFactor shouldBe new Base64URL("p")
      rsaKey.getSecondPrimeFactor shouldBe new Base64URL("q")
      rsaKey.getFirstFactorCRTExponent shouldBe new Base64URL("dp")
      rsaKey.getSecondFactorCRTExponent shouldBe new Base64URL("dq")
      rsaKey.getFirstCRTCoefficient shouldBe new Base64URL("qi")
      rsaKey.getOtherPrimes should have size 1

      val otherPrime = rsaKey.getOtherPrimes.get(0)

      otherPrime.getPrimeFactor shouldBe new Base64URL("r")
      otherPrime.getFactorCRTExponent shouldBe new Base64URL("d")
      otherPrime.getFactorCRTCoefficient shouldBe new Base64URL("t")
    }

    "write a private first and second representation RSAKey" in {
      val otherPrimes = new OtherPrimesInfo(new Base64URL("r"),
                                            new Base64URL("d"),
                                            new Base64URL("t"))

      val jwk = new RSAKey.Builder(new Base64URL("n"),
                                   new Base64URL("e")
                                  )
                                  .privateExponent(new Base64URL("d"))
                                  .firstPrimeFactor(new Base64URL("p"))
                                  .secondPrimeFactor(new Base64URL("q"))
                                  .firstFactorCRTExponent(new Base64URL("dp"))
                                  .secondFactorCRTExponent(new Base64URL("dq"))
                                  .firstCRTCoefficient(new Base64URL("qi"))
                                  .otherPrimes(List(otherPrimes))
                                  .build

      val json = jwk.toJson.asJsObject

      val n = json.getFields("n")
      n should have length 1
      n should contain theSameElementsAs Seq(JsString("n"))

      val e = json.getFields("e")
      e should have length 1
      e should contain theSameElementsAs Seq(JsString("e"))

      val d = json.getFields("d")
      d should have length 1
      d should contain theSameElementsAs Seq(JsString("d"))

      val p = json.getFields("p")
      p should have length 1
      p should contain theSameElementsAs Seq(JsString("p"))

      val q = json.getFields("q")
      q should have length 1
      q should contain theSameElementsAs Seq(JsString("q"))

      val dp = json.getFields("dp")
      dp should have length 1
      dp should contain theSameElementsAs Seq(JsString("dp"))

      val dq = json.getFields("dq")
      dq should have length 1
      dq should contain theSameElementsAs Seq(JsString("dq"))

      val qi = json.getFields("qi")
      qi should have length 1
      qi should contain theSameElementsAs Seq(JsString("qi"))

      val oth = json.getFields("oth")
      oth should have length 1
      oth should contain theSameElementsAs Seq(JsArray(JsObject("r" -> JsString("r"),
                                                                "d" -> JsString("d"),
                                                                "t" -> JsString("t"))))
    }

    "parse a public ECKey" in {
      val json =
        """
        {
          "kty" : "EC",
          "crv" : "P-256",
          "x"   : "x",
          "y"   : "y"
        }
        """.parseJson

      val ecKey = json.convertTo[ECKey]

      ecKey.getCurve shouldBe ECKey.Curve.P_256
      ecKey.getX shouldBe new Base64URL("x")
      ecKey.getY shouldBe new Base64URL("y")
    }

    "parse a private ECKey" in {
      val json =
        """
        {
          "kty" : "EC",
          "crv" : "P-256",
          "x"   : "x",
          "y"   : "y",
          "d"   : "d"
        }
        """.parseJson

      val ecKey = json.convertTo[ECKey]

      ecKey.getCurve shouldBe ECKey.Curve.P_256
      ecKey.getX shouldBe new Base64URL("x")
      ecKey.getY shouldBe new Base64URL("y")
      ecKey.getD shouldBe new Base64URL("d")
    }

    "write a private ECKey" in {
      val ecKey = new ECKey.Builder(ECKey.Curve.P_256,
                                    new Base64URL("x"),
                                    new Base64URL("y")
                                   )
                                   .d(new Base64URL("d"))
                                   .build

      val json = ecKey.toJson.asJsObject

      val crv = json.getFields("crv")
      crv should have length 1
      crv should contain theSameElementsAs Seq(JsString("P-256"))

      val x = json.getFields("x")
      x should have length 1
      x should contain theSameElementsAs Seq(JsString("x"))

      val y = json.getFields("y")
      y should have length 1
      y should contain theSameElementsAs Seq(JsString("y"))

      val d = json.getFields("d")
      d should have length 1
      d should contain theSameElementsAs Seq(JsString("d"))
    }

    "parse a JWKSet" in {
      val json =
        """
        {
          "keys" : [
            {
              "kty" : "EC",
              "kid" : "key1",
              "crv" : "P-256",
              "x"   : "x",
              "y"   : "y"
            },
            {
              "kty" : "RSA",
              "kid" : "key2",
              "n"   : "n",
              "e"   : "e"
            }
          ]
        }
        """.parseJson

      val jwkSet = json.convertTo[JWKSet]

      jwkSet.getKeys should have size 2

      val ecKey = jwkSet.getKeyByKeyId("key1")
      val rsaKey = jwkSet.getKeyByKeyId("key2")

      ecKey.getKeyType shouldBe KeyType.EC
      ecKey shouldBe an[ECKey]
      rsaKey.getKeyType shouldBe KeyType.RSA
      rsaKey shouldBe a[RSAKey]
    }

    "write a JWKSet" in {
      val rsaKey = new RSAKey.Builder(new Base64URL("n"),
                                      new Base64URL("e")
                                     )
                                     .build

      val ecKey = new ECKey.Builder(ECKey.Curve.P_256,
                                    new Base64URL("x"),
                                    new Base64URL("y")
                                   )
                                   .build

      val jwkSet = new JWKSet(List(rsaKey, ecKey))

      val json = jwkSet.toJson.asJsObject

      json.getFields("keys") match {
        case Seq(JsArray(jwks)) => jwks should have length 2
        case _ => fail("keys field was not a JsArray")
      }
    }
  }

}
