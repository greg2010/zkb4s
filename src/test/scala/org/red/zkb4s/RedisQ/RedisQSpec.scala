package org.red.zkb4s.RedisQ

import org.red.zkb4s.RedisQ.RedisQSchema.RootPackage
import org.red.zkb4s.RedisQ.RedisQSchema2CommonSchema.converter
import org.scalatest._
import io.circe.parser._
import io.circe.generic.auto._
import scala.concurrent.duration._

import scala.io.Source

class RedisQSpec extends FlatSpec with Matchers {
  private def parseJson(name: String): RootPackage = {
    val jsonStr: String = Source.fromInputStream(
      getClass.getResourceAsStream("/" + name)).getLines().mkString("\n")
    decode[RootPackage](jsonStr).right.get
  }

  "redisq schema 1" should "be parsed" in {
    converter(parseJson("redisq1.json").`package`.get)
  }
  "redisq schema 2" should "be parsed" in {
    converter(parseJson("redisq2.json").`package`.get)
  }
  "redisq schema null" should "be parsed" in {
    parseJson("null.json")
  }

  "redisq query" should "not fail" in {
    val redisq = new ReqisQAPI(queueId = "test", ttw = 1.seconds)
    println(s"Fetched kill with killId=${redisq.stream().next().right.get.killId}")
  }

}