package com.persona.service.offer

import com.persona.util.json.DateTimeJsonProtocol
import org.joda.time.DateTime
import spray.json.{NullOptions, DefaultJsonProtocol}

case class OfferBasicInfo(offerID: Int,
                          partnerName: String,
                          partnerID: Int,
                          partnerImageUrl: String,
                          offerDetails: String,
                          offerStatus: Int,
                          offerMinRank: Option[Int],
                          offerMaxParticipants: Int,
                          offerStartDate: DateTime,
                          offerExpirationDate: DateTime,
                          offerReward: Double,
                          offerCurrentParticipants: Int) {

}

trait OfferBasicInfoJsonProtocol extends DefaultJsonProtocol with NullOptions with DateTimeJsonProtocol {

  implicit val offerBasicJsonParser = jsonFormat12(OfferBasicInfo)

}
