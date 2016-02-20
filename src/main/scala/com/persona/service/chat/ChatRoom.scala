package com.persona.service.chat

import java.util.UUID

import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, OverflowStrategy}
import akka.stream.scaladsl._
import com.persona.service.chat.dao.ChatDAO
import org.joda.time.DateTime
import spray.json._
import scala.concurrent.duration._

class ChatRoom(id: UUID, actorSystem: ActorSystem, chatDAO: ChatDAO) extends ChatJsonProtocol {

//  import actorSystem.dispatcher
//  actorSystem.scheduler.schedule(10 seconds, 10 seconds) {
//    chatActor ! ChatMessage("system", "alive", new DateTime)
//  }

  private[this] val chatActor = actorSystem.actorOf(Props(classOf[ChatRoomActor], id, chatDAO))

  def websocketFlow(user: String) = {
    Flow.fromGraph(
      GraphDSL.create(Source.actorRef[ChatMessage](bufferSize = 100, OverflowStrategy.dropHead)) {
        implicit builder => {
          chatSource => {
            import GraphDSL.Implicits._

            val fromSocket = builder.add(
              Flow[Message].collect {
                case TextMessage.Strict(txt) => {
                  val jsonObj = txt.parseJson.asJsObject
                  val receiver = jsonObj.fields("receiver").convertTo[String]
                  val msg = jsonObj.fields("msg").convertTo[String]
                  ChatMessage(user, msg, new DateTime)
                }
              }
            )

            val toSocket = builder.add(
              Flow[ChatMessage].map {
                case msg:ChatMessage => TextMessage(msg.toJson.compactPrint)
              }
            )

            val merge = builder.add(Merge[ChatEvent](2))

            val chatActorSink = Sink.actorRef[ChatEvent](chatActor, Disconnect(user, new DateTime))

            val actorSource = builder.materializedValue.map(ref => Connect(user, new DateTime, ref))

            fromSocket ~> merge.in(0)
            actorSource ~> merge.in(1)

            merge ~> chatActorSink
            chatSource ~> toSocket

            FlowShape(fromSocket.in, toSocket.out)
          }
        }
      }
    )
  }

}

object ChatRoom {

  var chatRooms = Map.empty[UUID, ChatRoom]

  def find(id: UUID)(implicit actorSystem: ActorSystem): Option[ChatRoom] = chatRooms.get(id)

  def createRoom(id: UUID)(implicit actorSystem: ActorSystem, chatDAO: ChatDAO): Unit = {
    val room = new ChatRoom(id, actorSystem, chatDAO)
    chatRooms += id -> room
  }

}