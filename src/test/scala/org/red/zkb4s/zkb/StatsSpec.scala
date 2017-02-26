package org.red.zkb4s.zkb

import io.circe.generic.auto._
import io.circe.parser._
import org.red.zkb4s.zkb.StatsTypes.SuperPilot
import org.scalatest.{FlatSpec, MustMatchers}

import scala.io.Source

/**
  * Created by Andi on 20/01/2016.
  */
class StatsSpec extends FlatSpec with MustMatchers {

  "alliance stats" should "be parsed" in {
    val json        = Source.fromInputStream(getClass.getResourceAsStream("/alliancestats.json")).getLines().mkString("\n")
    val r           = decode[StatsTypes.AllianceInfo](json).right.get
    val superpilots = r.supers.supercarriers.data.toList.flatten ++ r.supers.titans.data.toList.flatten
    superpilots.size must equal(199)
    superpilots.head must equal(new SuperPilot(105, 93260759, "Doctor Chalke"))
  }

  "alliance stats 2" should "be parsed" in {
    val json = Source.fromInputStream(getClass.getResourceAsStream("/alliancestats2.json")).getLines().mkString("\n")
    val r    = decode[StatsTypes.AllianceInfo](json)
    println(r)
    val r2          = r.right.get
    val superpilots = r2.getSupers ++ r2.getTitans
    superpilots.isEmpty must equal(true)
  }

  "alliance stats 3" should "be parsed" in {
    val json        = Source.fromInputStream(getClass.getResourceAsStream("/alliancestats3.json")).getLines().mkString("\n")
    val r           = decode[StatsTypes.AllianceInfo](json).right.get
    val superpilots = r.getSupers ++ r.getTitans
    println(superpilots)
  }

  "character stats 1" should "be parsed" in {
    val json        = Source.fromInputStream(getClass.getResourceAsStream("/characterstats1.json")).getLines().mkString("\n")
    val r           = decode[StatsTypes.CharacterInfo](json)
    r.isRight must equal(true)
  }

  "corporation stats 1" should "be parsed" in {
    val json = Source.fromInputStream(getClass.getResourceAsStream("/corporationstats1.json")).getLines().mkString("\n")
    val r = decode[StatsTypes.CorporationInfo](json)
    r.isRight must equal(true)
    r.right.get.activepvp.flatMap(_.characters).get.count must equal (107)
  }
}
