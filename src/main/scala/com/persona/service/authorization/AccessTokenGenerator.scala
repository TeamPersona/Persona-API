package com.persona.service.authorization

import com.persona.service.account.Account
import com.persona.service.account.thirdparty.ThirdPartyAccount

trait AccessTokenGenerator {

  def generate(account: Account, thirdPartyAccount: ThirdPartyAccount, expirationTime: Int): String

  def verify(accessToken: String): Option[(Int, String)]

}
