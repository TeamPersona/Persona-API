package com.persona.service.account.thirdparty

import org.joda.time.DateTime

case class ThirdPartyAccount(
  id: String,
  secret: String,
  creationTime: DateTime
)
