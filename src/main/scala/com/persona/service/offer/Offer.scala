package com.persona.service.offer

import java.util.UUID

import com.persona.util.json.{DateTimeJsonProtocol, UuidJsonProtocol}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spray.json._

import scala.concurrent.Future

case class Offer(val id: UUID, // This is a timeUUID that has the exact time it was created
                 creationDay: DateTime, // TODO: may have to change to string type/check to match Cassandra
                 description: String,
                 expirationTime: DateTime,
                 currentParticipants: Int,
                 maxParticipants: Int,
                 value: Double,
                 criteria: Map[String, String])

trait OfferJsonProtocol extends DefaultJsonProtocol with UuidJsonProtocol with DateTimeJsonProtocol {

  implicit val dataItemJsonParser = jsonFormat8(Offer)

}
