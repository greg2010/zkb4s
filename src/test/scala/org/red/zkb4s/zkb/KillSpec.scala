package org.red.zkb4s.zkb

import io.circe.generic.auto._
import io.circe.parser._
import org.red.zkb4s.zkb.ZkillboardSchema.Killmail
import ZkillboardSchema2CommonSchema.converter
import org.scalatest.{FlatSpec, MustMatchers}

import scala.io.Source

/**
  * Created by andi on 27/12/2016.
  */
class KillSpec extends FlatSpec with MustMatchers {

  "the circe parser" should "parse things from the API" in {
    val json = Source.fromInputStream(getClass.getResourceAsStream("/normalkills.json")).getLines()
    val res = json.map { line =>
      decode[Killmail](line)
    }.toList
    res.count(_.isRight) must equal(200)
    res.map(x => converter(x.right.get))
  }

}
