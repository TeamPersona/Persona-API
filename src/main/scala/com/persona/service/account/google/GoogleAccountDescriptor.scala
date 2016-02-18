package com.persona.service.account.google

import com.persona.service.account.AccountDescriptor

case class GoogleAccountDescriptor(
  accountDescriptor: AccountDescriptor,
  googleId: String
)
