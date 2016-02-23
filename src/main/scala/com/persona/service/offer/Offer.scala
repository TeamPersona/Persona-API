package com.persona.service.offer

import org.joda.time.DateTime
import spray.json._

case class Offer(val offerID: Int,
                 val partnerName: String,
                 val partnerID: Int,
                 val partnerImageUrl: String,
                 val offerDetails: String,
                 val offerStatus: Int,
                 val offerMinRank: Option[Int],
                 val offerMaxParticipants: Int,
                 val offerStartDate: DateTime,
                 val offerExpirationDate: DateTime,
                 val offerReward: Double,
                 val offerCurrentParticipants: Int,
                 val offerType: List[String],
                 val offerFilters: Map[String, List[Map[String, String]]],
                 val offerInfoRequired: Map[String, List[Map[String, String]]],
                 val isEligible: Boolean,
                 val isParticipating: Boolean) {

  def this (offerBasicInfo: OfferBasicInfo, offerType: List[String], offerFilters: Map[String, List[Map[String, String]]], offerInfoRequired: Map[String, List[Map[String, String]]], isEligible: Boolean, isParticipating: Boolean) {
    this (offerBasicInfo.offerID, offerBasicInfo.partnerName, offerBasicInfo.partnerID, offerBasicInfo.partnerImageUrl, offerBasicInfo.offerDetails, offerBasicInfo.offerStatus, offerBasicInfo.offerMinRank, offerBasicInfo.offerMaxParticipants, offerBasicInfo.offerStartDate, offerBasicInfo.offerExpirationDate, offerBasicInfo.offerReward, offerBasicInfo.offerCurrentParticipants, offerType, offerFilters, offerInfoRequired, isEligible, isParticipating)
  }
}

trait OfferJsonProtocol extends DefaultJsonProtocol with OfferBasicInfoJsonProtocol {

  implicit val offerJsonParser = jsonFormat17(Offer.apply)

}
