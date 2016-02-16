package com.persona.util.http

import java.util.Locale

import akka.actor.{Actor, ActorRef}
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.headers.{Expires, `Cache-Control`}
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, HttpResponse}
import akka.pattern.pipe
import akka.stream.scaladsl.ImplicitMaterializer
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, Seconds}

import scala.concurrent.duration._

object HttpRetriever {

  private object Refresh

  private val DefaultRefresh = 10.minutes.toSeconds
  private val RefreshSkew = 5.minutes.toSeconds
  private val MinimumRefresh = 15.seconds.toSeconds

  private val HttpDateFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                                             .withZoneUTC()
                                             .withLocale(Locale.US)

}

class HttpRetriever(recipient: ActorRef, http: HttpExt, targetUri: String)
  extends Actor
    with ImplicitMaterializer {

  private[this] implicit val executionContext = context.dispatcher

  def receive: Receive = {
    case HttpRetriever.Refresh =>
      refresh()

    case response: HttpResponse =>
      handleResponse(response)
  }

  override def preStart(): Unit = {
    refresh()
  }

  private[this] def refresh() = {
    val httpRequest = HttpRequest(uri = targetUri)

    http.singleRequest(httpRequest).pipeTo(self)
  }

  private[this] def handleResponse(response: HttpResponse) = {
    recipient ! response

    val cacheTime = calculateCacheTime(response.headers)
    val skewedCacheTime = cacheTime - HttpRetriever.RefreshSkew
    val refreshTime =
      if(cacheTime > 0) math.max(HttpRetriever.MinimumRefresh, skewedCacheTime)
      else HttpRetriever.DefaultRefresh

    context.system.scheduler.scheduleOnce(refreshTime.seconds, self, HttpRetriever.Refresh)
  }

  private[this] def calculateCacheTime(httpHeaders: Seq[HttpHeader]) = {
    // As per HTTP specification, "max-age" overrides "expires"
    getMaxAge(httpHeaders).getOrElse {
      getTimeTillExpiration(httpHeaders).getOrElse {
        0 // No cache time specified
      }
    }
  }

  private[this] def getMaxAge(httpHeaders: Seq[HttpHeader]) = {
    val maybeHeader = httpHeaders.find { header =>
      header.is(`Cache-Control`.lowercaseName)
    }

    maybeHeader.flatMap { header =>
      val maybeMaxAge = header.value.split(",").find { string =>
        string.startsWith("max-age") || string.startsWith(" max-age")
      }

      maybeMaxAge.map { maxAge =>
        val splitMaxAge = maxAge.split("=")

        if(2 == splitMaxAge.length) {
          splitMaxAge(1).toInt
        } else {
          0
        }
      }
    }
  }

  private[this] def getTimeTillExpiration(httpHeaders: Seq[HttpHeader]) = {
    val maybeHeader = httpHeaders.find(header => header.is(Expires.lowercaseName))

    maybeHeader.map { header =>
      val expirationDate = HttpRetriever.HttpDateFormat.parseDateTime(header.value)

      Seconds.secondsBetween(DateTime.now, expirationDate).getSeconds
    }
  }

}
