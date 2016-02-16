package com.persona.util.openid

import spray.json._

trait DiscoveryDocumentJsonProtocol extends DefaultJsonProtocol {

  implicit object DiscoveryDocumentJsonFormat extends RootJsonFormat[DiscoveryDocument] {

    def readIssuer(openIdDiscoveryDocument: JsObject): String = {
      openIdDiscoveryDocument.getFields("issuer") match {
        case Seq(JsString(issuer)) => issuer
        case _ => throw new DeserializationException("Invalid issuer")
      }
    }

    def writeIssuer(openIdDiscoveryDocument: DiscoveryDocument): JsField = {
      "issuer" -> JsString(openIdDiscoveryDocument.issuer)
    }

    def readJwksUri(openIdDiscoveryDocument: JsObject): String = {
      openIdDiscoveryDocument.getFields("jwks_uri") match {
        case Seq(JsString(jwksUri)) => jwksUri
        case _ => throw new DeserializationException("Invalid jwks_uri")
      }
    }

    def writeJwksUri(openIdDiscoveryDocument: DiscoveryDocument): JsField = {
      "jwks_uri" -> JsString(openIdDiscoveryDocument.jwksUri)
    }

    def read(openIdDiscoveryDocument: JsValue): DiscoveryDocument = {
      val openIdDiscoveryDocumentAsJsObject = openIdDiscoveryDocument.asJsObject

      val issuer = readIssuer(openIdDiscoveryDocumentAsJsObject)
      val jwksUri = readJwksUri(openIdDiscoveryDocumentAsJsObject)

      new DiscoveryDocument(issuer, jwksUri)
    }

    def write(openIdDiscoveryDocument: DiscoveryDocument): JsValue = {
      val issuer = writeIssuer(openIdDiscoveryDocument)
      val jsonJwksUri = writeJwksUri(openIdDiscoveryDocument)

      JsObject(
        issuer,
        jsonJwksUri
      )
    }

  }

}
