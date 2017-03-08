package org.red.zkb4s.RedisQ

import com.typesafe.scalalogging.LazyLogging
import io.circe.parser._
import io.circe.generic.auto._
import org.red.zkb4s.RedisQ.RedisQSchema._
import org.red.zkb4s.RedisQ.RedisQSchema2CommonSchema.converter
import org.red.zkb4s.schema.CommonSchemas

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.control.NoStackTrace
import scalaj.http.{Http, HttpRequest, HttpResponse}
import scalaz.concurrent.Task

class ReqisQAPI(queueId: String = "", ttw: FiniteDuration = 10.seconds, customUserAgent: String = "Unknown Application") extends LazyLogging {

  private class EmptyPackageBodyException extends RuntimeException with NoStackTrace

  private val url = "https://redisq.zkillboard.com/listen.php?" +
    s"queueID=${queueId}&" +
    s"ttw=${ttw.toSeconds}"
  private val userAgent = "red-zkbapi/1.0" + " " + customUserAgent
  private val httpRequest: HttpRequest = Http(url)
    .method("GET")
    .header("User-Agent", userAgent)
    .timeout(ttw.toMillis.toInt + 2, ttw.toMillis.toInt + 2)

  private val requestsBeforeBackoff = 10

  def poll(): Task[HttpResponse[String]] = {
    Task(httpRequest.asString)
  }

  def stream(): Iterator[Either[Throwable, CommonSchemas.Killmail]] =
    new Iterator[Either[Throwable, CommonSchemas.Killmail]] {
      def hasNext = true

      @tailrec
      def next(): Either[Throwable, CommonSchemas.Killmail] = {
        poll().unsafePerformSyncAttempt.toEither
          .flatMap(r => parse(r.body))
          .flatMap(_.as[RootPackage])
          .flatMap {
            _.`package` match {
              case Some(res) => Right(converter(res))
              case None => Left(new EmptyPackageBodyException)
            }
          } match {
          case Left(ex: EmptyPackageBodyException) => next()
          case res => res
        }
      }
    }
}