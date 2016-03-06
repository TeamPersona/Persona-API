package com.persona.service.offer

import org.joda.time.DateTime

class OfferBasicInfoSchema private(val offerID: Int,
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
                                   val offerCurrentParticipants: Int) {

}

object OfferBasicInfoSchema {

  def apply(offerID: Int, partnerName: String, partnerID: Int, partnerImageUrl: String, offerDetails: String, offerStatus: Int, offerMinRank: Option[Int], offerMaxParticipants: Int, offerStartDate: DateTime, offerExpirationDate: DateTime, offerReward: Double, offerCurrentParticipants: Int) : OfferBasicInfoSchema = {

    new OfferBasicInfoSchema(offerID, partnerName, partnerID, partnerImageUrl, offerDetails, offerStatus, offerMinRank, offerMaxParticipants, offerStartDate, offerExpirationDate, offerReward, offerCurrentParticipants)
  }

}
