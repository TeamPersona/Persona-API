package com.persona.service.chat.dao

import java.util.UUID
import slick.driver.PostgresDriver.api._

case class MsgHistory(
  msgId: UUID,
  userid: String,
  msgType: Int,
  message: String
)
//
//class MsgHistoryTable(tag: Tag) extends Table[MsgHistory](tag, "msg_history") with DatabaseConfig {
//
//  def msgId: Rep[UUID]("msg_id")
//  def userId: Rep[String]("userid")
//  def msgType: Rep[Int]("type")
//  def message: Rep[String]("message")
//
//
//  override def * : (msgId, userId, msgType, message)
//}