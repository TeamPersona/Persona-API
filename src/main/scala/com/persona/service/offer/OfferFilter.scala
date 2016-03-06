//package com.persona.service.offer
//
//import spray.json.DefaultJsonProtocol
//
//case class OfferFilter (val filters: List[Map[String, String]]) {
//
//}
//
//trait OfferFilterJsonProtocol extends DefaultJsonProtocol  {
//
//  implicit val offerFilterJsonParser = jsonFormat1(OfferFilter)
//
//}