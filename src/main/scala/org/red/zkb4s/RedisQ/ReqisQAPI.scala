package org.red.zkb4s.RedisQ

import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.middleware.{Retry, RetryPolicy}
import org.red.zkb4s.RedisQ.RedisQSchema._
import org.red.zkb4s.RedisQ.RedisQSchema2CommonSchema.converter
import org.red.zkb4s.schema.CommonSchemas

import scala.annotation.tailrec
import scala.concurrent.duration._
import scalaz.concurrent.Task

class ReqisQAPI(queueId: String = "", ttw: FiniteDuration = 10.seconds, customUserAgent: String = "Unknown Application") extends LazyLogging {

  private val url: Uri = Uri.unsafeFromString("https://redisq.zkillboard.com/listen.php?" +
    s"queueID=${queueId}&" +
    s"ttw=${ttw.toSeconds}")
  private val userAgent = "red-zkbapi/1.0" + " " + customUserAgent

  private val requestsBeforeBackoff = 10

  def poll()(implicit c: Client): Task[RootPackage] = {
    val req = Request(uri = url)
      .putHeaders(Header("User-Agent", userAgent))

    implicit val jdec = jsonOf[RootPackage]

    val retryClient = Retry(RetryPolicy.exponentialBackoff(ttw, requestsBeforeBackoff))(c)

    retryClient.fetchAs[RootPackage](req)
  }

  def stream()(implicit c: Client): Iterator[CommonSchemas.Killmail] =
    new Iterator[CommonSchemas.Killmail] {
      def hasNext = true

      @tailrec
      def next(): CommonSchemas.Killmail = {
        poll().unsafePerformSyncFor(ttw + 2.seconds).`package` match {
          case Some(p) => p
          case None => next()
        }
      }
    }
}