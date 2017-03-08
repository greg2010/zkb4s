package org.red.zkb4s.RedisQ

import java.io.IOException

import com.typesafe.scalalogging.LazyLogging
import io.circe.ParsingFailure
import io.circe.parser._
import io.circe.generic.auto._
import org.red.zkb4s.RedisQ.RedisQSchema._
import org.red.zkb4s.RedisQ.RedisQSchema2CommonSchema.converter
import org.red.zkb4s.schema.CommonSchemas

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.control.NoStackTrace
import scalaj.http.{Http, HttpRequest}
import scalaz.concurrent.Task

class ReqisQAPI(queueId: String = "", ttw: FiniteDuration = 10.seconds, customUserAgent: String = "Unknown Application") extends LazyLogging {

  private class RetriableException(code: Int) extends IOException with NoStackTrace

  private case class EmptyPackageBodyException() extends RetriableException(-1)
  private case class TooManyRequestsException(retryAfter: Int) extends RetriableException(429)

  private val url = "https://redisq.zkillboard.com/listen.php?" +
    s"queueID=${queueId}&" +
    s"ttw=${ttw.toSeconds}"
  private val userAgent = "red-zkbapi/1.0" + " " + customUserAgent
  private val httpRequest: HttpRequest = Http(url)
    .method("GET")
    .header("User-Agent", userAgent)
    .timeout(ttw.toMillis.toInt + 2, ttw.toMillis.toInt + 2)

  private val requestsBeforeBackoff = 10
  private val backoffSequence = generateBackoffDelays(requestsBeforeBackoff, 10.seconds)

  private def isRetriable(ex: Throwable): Boolean = {
    ex.isInstanceOf[RetriableException] || ex.isInstanceOf[ParsingFailure]
  }

  private def generateBackoffDelays(numRequests: Int, ceil: Duration): Seq[Duration] = {
    val startingDuration = 100
    def genSeqInternal(numIterations: Int, acc: Seq[Duration]): Seq[Duration] = {
      numIterations match {
        case 0 => acc
        case _ => math.pow(startingDuration, numIterations).milliseconds.min(ceil) +: acc
      }
    }
    genSeqInternal(numRequests, Seq())
  }

  def poll(): Task[Either[Throwable, CommonSchemas.Killmail]] = {
    Task {
      val r = httpRequest.asString
      (r.code match {
        case 200 => parse(r.body)
        case 429 => Left(TooManyRequestsException(r.header("Retry-After").getOrElse("1000").toInt))
        case err => Left(new IOException(s"Error ${err}"))
      }).flatMap(_.as[RootPackage])
        .flatMap {
          _.`package` match {
            case Some(res) => Right(converter(res))
            case None => Left(new EmptyPackageBodyException)
          }
        }
    }
  }

  def pollRetriable(): Task[Either[Throwable, CommonSchemas.Killmail]] = {
    poll().retry(backoffSequence, isRetriable)
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

  def streamRetriable(): Iterator[Either[Throwable, CommonSchemas.Killmail]] = {
    new Iterator[Either[Throwable, CommonSchemas.Killmail]] {
      def hasNext = true

      def next(): Either[Throwable, CommonSchemas.Killmail] = {
        poll().unsafePerformSyncAttempt.toEither.joinRight
      }
    }
  }
}