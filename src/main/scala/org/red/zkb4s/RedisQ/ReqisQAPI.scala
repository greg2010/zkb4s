package org.red.zkb4s.RedisQ

import java.io.IOException

import com.typesafe.scalalogging.LazyLogging
import io.circe.{Json, ParsingFailure}
import io.circe.parser._
import io.circe.generic.auto._
import org.red.zkb4s.RedisQ.RedisQSchema._
import org.red.zkb4s.RedisQ.RedisQSchema2CommonSchema.converter
import org.red.zkb4s.schema.CommonSchemas

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.Try
import scala.util.control.NoStackTrace
import scalaj.http.{Http, HttpRequest, HttpStatusException}
import scalaz.concurrent.Task

class ReqisQAPI(queueId: String = "", ttw: FiniteDuration = 10.seconds, customUserAgent: String = "Unknown Application") extends LazyLogging {

  private class RetriableException extends IOException with NoStackTrace

  private class EmptyPackageBodyException extends RetriableException

  private val url = "https://redisq.zkillboard.com/listen.php?" +
    s"queueID=${queueId}&" +
    s"ttw=${ttw.toSeconds}"
  private val userAgent = "red-zkbapi/1.0" + " " + customUserAgent
  private val httpRequest: HttpRequest = Http(url)
    .method("GET")
    .header("User-Agent", userAgent)
    .timeout((ttw + 2.seconds).toMillis.toInt, (ttw + 2.seconds).toMillis.toInt)

  def poll(): Task[Either[Throwable, CommonSchemas.Killmail]] = {
    Task {
      val r = httpRequest.asString
      (r.code match {
        case 200 => parse(r.body)
        case _ if r.isError => Left(Try(r.throwError).toEither.left
          .getOrElse(new RuntimeException("No exception was thrown by .throwError!")))
        case x => Left(new IOException(s"Err ${x}"))
      })
        .flatMap(_.as[RootPackage])
        .flatMap {
          _.`package` match {
            case Some(res) => Right(converter(res))
            case None => Left(new EmptyPackageBodyException)
          }
        }
    }
  }

  def stream(): Iterator[Either[Throwable, CommonSchemas.Killmail]] = {
    new Iterator[Either[Throwable, CommonSchemas.Killmail]] {
      def hasNext = true

      @tailrec
      def next(): Either[Throwable, CommonSchemas.Killmail] = {
        poll().unsafePerformSyncAttempt.toEither.joinRight match {
          case Left(ex: EmptyPackageBodyException) => next()
          case x => x
        }
      }
    }
  }
}