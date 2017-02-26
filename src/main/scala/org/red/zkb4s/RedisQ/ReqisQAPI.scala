package org.red.zkb4s.RedisQ

import org.red.zkb4s.RedisQ.RedisQSchema._
import org.red.zkb4s.RedisQ.RedisQSchema2CommonSchema.converter
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client

import scala.concurrent.duration._
import scala.annotation.tailrec
import scalaz.concurrent.Task

class ReqisQAPI(queueId: String = "", ttw: Int = 10, customUserAgent: String = "Unknown Application") extends LazyLogging {

  private val url: Uri = Uri.unsafeFromString("https://redisq.zkillboard.com/listen.php?" +
    s"queueID=${queueId}&" +
    s"ttw=${ttw}")
  private val userAgent = "red-zkbapi/1.0" + " " + customUserAgent

  def poll()(implicit c: Client): Task[RootPackage] = {
    val req = Request(uri = url)
      .putHeaders(Header("User-Agent", userAgent))

    implicit val jdec = jsonOf[RootPackage]

    c.fetchAs[RootPackage](req)
  }

  def stream()(implicit c: Client): Iterator[org.red.zkb4s.CommonSchemas.Killmail] =
    new Iterator[org.red.zkb4s.CommonSchemas.Killmail] {
    def hasNext = true
    @tailrec
    def next(): org.red.zkb4s.CommonSchemas.Killmail = {
      poll().unsafePerformSyncFor((ttw + 2).seconds).`package` match {
        case Some(p) => p
        case None => next()
      }
    }
  }
}