package com.persona.service.offer

import org.joda.time.DateTime

class OfferBasicInfoSchema private(val offerID: Int,
                                   val partnerName: String,
                                   val partnerID: Int,
                                   val partnerImageUrl: String,
                                   val offerDetails: String,
                                   val offerCategory: String,
                                   val offerType: String,
                                   val offerStatus: String,
                                   val rewardTier: Option[Int],
                                   val maxParticipants: Int,
                                   val startTime: DateTime,
                                   val endTime: DateTime,
                                   val reward: Double,
                                   val currentParticipants: Int) {

}

object OfferBasicInfoSchema {

  def apply(offerID: Int, partnerName: String, partnerID: Int, partnerImageUrl: String, offerDetails: String, offerCategory: String, offerType: String, offerStatus: String, rewardTier: Option[Int], maxParticipants: Int, startTime: DateTime, endTime: DateTime, reward: Double, currentParticipants: Int) : OfferBasicInfoSchema = {
    new OfferBasicInfoSchema(offerID, partnerName, partnerID, partnerImageUrl, offerDetails, offerCategory, offerType, offerStatus, rewardTier, maxParticipants, startTime, endTime, reward, currentParticipants)
  }

}
