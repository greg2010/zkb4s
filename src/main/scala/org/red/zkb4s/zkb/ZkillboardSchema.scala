package org.red.zkb4s.zkb

import scala.language.implicitConversions

import java.text.SimpleDateFormat
import java.util.Date


object ZkillboardSchema {

  case class Victim(
      shipTypeID: Long,
      characterID: Long,
      characterName: String,
      corporationID: Long,
      corporationName: String,
      allianceID: Long,
      allianceName: String,
      factionID: Long,
      factionName: String,
      damageTaken: Long
  )

  case class Attackers(
      characterID: Long,
      characterName: String,
      corporationID: Long,
      corporationName: String,
      allianceID: Long,
      allianceName: String,
      factionID: Long,
      factionName: String,
      securityStatus: Double,
      damageDone: Double,
      finalBlow: Long,
      weaponTypeID: Long,
      shipTypeID: Long
  )

  case class Items(
      typeID: Long,
      flag: Long,
      qtyDropped: Long,
      qtyDestroyed: Long,
      singleton: Long
  )

  case class Zkb(
      hash: String,
      points: Long,
      totalValue: Double
  )

  case class Position(x: Double, y: Double, z: Double)

  case class Killmail(
      killID: Long,
      solarSystemID: Long,
      killTime: String,
      moonID: Long,
      victim: Victim,
      position: Option[Position],
      attackers: List[Attackers],
      items: List[Items],
      zkb: Zkb
  )
}

object ZkillboardSchema2CommonSchema {

  import  ZkillboardSchema._

  private def string2Date(s: String): Date = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s)
  }

  private implicit def item2item(item: Items): org.red.zkb4s.CommonSchemas.Item = {
    org.red.zkb4s.CommonSchemas.Item(
      itemId = item.typeID,
      quantityDestroyed = item.qtyDestroyed,
      quantityDropped = item.qtyDropped
    )
  }

  private implicit def victim2victim(victim: Victim): org.red.zkb4s.CommonSchemas.Victim = {
    org.red.zkb4s.CommonSchemas.Victim(
      shipId = victim.shipTypeID,
      character = org.red.zkb4s.CommonSchemas.Character(
        characterId = Some(victim.characterID),
        corporationId = Some(victim.corporationID),
        allianceId = Some(victim.allianceID)),
      items = List(),
      damageTaken = victim.damageTaken
    )
  }

  private implicit def attacker2attacker(attacker: Attackers): org.red.zkb4s.CommonSchemas.Attacker = {
    org.red.zkb4s.CommonSchemas.Attacker(
      shipId = Some(attacker.shipTypeID),
      character = org.red.zkb4s.CommonSchemas.Character(
        characterId = Some(attacker.characterID),
        corporationId = Some(attacker.corporationID),
        allianceId = Some(attacker.allianceID)),
      weaponType = Some(attacker.weaponTypeID),
      damageDone = attacker.damageDone.toLong,
      finalBlow = attacker.finalBlow == attacker.characterID,
      securityStatus = attacker.securityStatus)
  }

  private implicit def position2position(position: Option[Position]): Option[org.red.zkb4s.CommonSchemas.Position] = {
    position match {
      case Some(posn) => {
        Some(org.red.zkb4s.CommonSchemas.Position(
          x = posn.x,
          y = posn.y,
          z = posn.z
        ))
      }
      case None => None
    }
  }

  private implicit def zkb2zkbMetaData(zkb: Zkb): org.red.zkb4s.CommonSchemas.ZkbMetaData = {
    org.red.zkb4s.CommonSchemas.ZkbMetaData(
      hash = zkb.hash,
      totalValue = zkb.totalValue,
      points = zkb.points)
  }

  implicit def converter(killmail: Killmail): org.red.zkb4s.CommonSchemas.Killmail = {
    org.red.zkb4s.CommonSchemas.Killmail(
      killId = killmail.killID,
      killTime = string2Date(killmail.killTime),
      victim = victim2victim(killmail.victim).copy(items = killmail.items.map(item2item)),
      attackers = killmail.attackers.map(attacker2attacker),
      solarSystem = killmail.solarSystemID,
      position = killmail.position,
      zkbMetadata = killmail.zkb
    )
  }
}