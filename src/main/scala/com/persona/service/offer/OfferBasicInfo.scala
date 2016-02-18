package com.persona.service.offer

import com.persona.util.json.DateTimeJsonProtocol
import org.joda.time.DateTime
import spray.json.{NullOptions, DefaultJsonProtocol}

case class OfferBasicInfo(offerID: Int,
                          partnerName: String,
                          partnerID: Int,
                          partnerImageUrl: String,
                          offerDetails: String,
                          offerCategory: String,
                          offerType: String,
                          offerStatus: String,
                          rewardTier: Option[Int],
                          maxParticipants: Int,
                          startTime: DateTime,
                          endTime: DateTime,
                          reward: Double,
                          currentParticipants: Int) {

}

trait OfferBasicInfoJsonProtocol extends DefaultJsonProtocol with NullOptions with DateTimeJsonProtocol {

  implicit val offerBasicJsonParser = jsonFormat14(OfferBasicInfo)

}
