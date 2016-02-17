package com.persona

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object Main extends App {

  private[this] implicit val actorSystem = ActorSystem()
  private[this] implicit val executionContext = actorSystem.dispatcher
  private[this] implicit val materializer = ActorMaterializer()

  private[this] val config = ConfigFactory.load()
  private[this] val http = Http()
  private[this] val bootstrap = new Bootstrap(config, http)

  private[this] val httpConfig = config.getConfig("http")
  private[this] val interface = httpConfig.getString("interface")
  private[this] val port = httpConfig.getInt("port")

  // Start the server
  val binding = http.bindAndHandle(bootstrap.routes, interface, port)

  // Wait for someone to stop the server
  StdIn.readLine()

  // Stop the server
  binding.flatMap(_.unbind())
         .onComplete(_ => actorSystem.shutdown())

}
