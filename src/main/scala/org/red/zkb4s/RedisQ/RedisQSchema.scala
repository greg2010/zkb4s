package org.red.zkb4s.RedisQ

import scala.language.implicitConversions

import  RedisQSchema._
import java.text.SimpleDateFormat
import java.util.Date

object RedisQSchema {

  case class RootPackage(`package`: Option[KillPackage])

  case class KillPackage(killID: Long,
                         killmail: Killmail,
                         zkb: Zkb)

  case class Killmail(solarSystem: EntityDef,
                      killID: Long,
                      killTime: String,
                      attackers: List[Attacker],
                      attackerCount: Long,
                      victim: Victim)

  case class EntityDef(id: Long, name: Option[String])

  case class WeaponType(id: Option[Long], name: String)

  // id is [None] if WeaponType is Ship

  case class SolarSystem(id: Long)

  case class Attacker(character: Option[EntityDef], // Null if attacker is NPC
                      corporation: Option[EntityDef], // Null if NPC
                      alliance: Option[EntityDef], // Null if no alliance
                      shipType: Option[EntityDef], // Null if NPC (?)
                      weaponType: Option[WeaponType], // Null if unknown (?)
                      damageDone: Long,
                      finalBlow: Boolean,
                      securityStatus: Double)

  case class Position(y: Double, x: Double, z: Double)

  case class Item(itemType: EntityDef,
                  quantityDestroyed: Option[Long], // Null if none destroyed
                  quantityDropped: Option[Long])

  // Null if none dropped

  case class Victim(character: Option[EntityDef], // Null if structure
                    corporation: EntityDef,
                    alliance: Option[EntityDef], // Null if no alliance
                    shipType: EntityDef,
                    damageTaken: Long,
                    items: Option[List[Item]],
                    position: Position)

  case class Zkb(locationID: Long,
                 hash: String,
                 totalValue: Double,
                 points: Long)

}

object RedisQSchema2CommonSchema {

  private def string2Date(s: String): Date = {
    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(s)
  }

  private implicit def optionEntity2id(entity: Option[EntityDef]): Option[Long] = {
    entity match {
      case Some(ent) => Some(ent.id)
      case None => None
    }
  }

  private implicit def entity2id(entity: EntityDef): Long = {
    entity.id
  }

  private implicit def item2item(item: Item): org.red.zkb4s.CommonSchemas.Item = {
    org.red.zkb4s.CommonSchemas.Item(
      itemId = item.itemType,
      quantityDestroyed = item.quantityDestroyed.getOrElse(0),
      quantityDropped = item.quantityDropped.getOrElse(0)
    )
  }

  private implicit def victim2victim(victim: Victim): org.red.zkb4s.CommonSchemas.Victim = {
    org.red.zkb4s.CommonSchemas.Victim(
      shipId = victim.shipType,
      character = org.red.zkb4s.CommonSchemas.Character(
        characterId = victim.character,
        corporationId = Some(victim.corporation.id),
        allianceId = victim.alliance),
      items = victim.items.getOrElse(List()).map(item2item),
      damageTaken = victim.damageTaken
    )
  }

  private implicit def attacker2attacker(attacker: Attacker): org.red.zkb4s.CommonSchemas.Attacker = {
    org.red.zkb4s.CommonSchemas.Attacker(
      shipId = attacker.shipType,
      character = org.red.zkb4s.CommonSchemas.Character(
        characterId = attacker.character,
        corporationId = attacker.corporation,
        allianceId = attacker.alliance),
      weaponType = attacker.weaponType.getOrElse(WeaponType(None, "")).id,
      damageDone = attacker.damageDone,
      finalBlow = attacker.finalBlow,
      securityStatus = attacker.securityStatus)
  }

  private implicit def position2position(position: Position): Option[org.red.zkb4s.CommonSchemas.Position] = {

    Some(org.red.zkb4s.CommonSchemas.Position(
      x = position.x,
      y = position.y,
      z = position.z))
  }

  private implicit def zkb2zkbMetaData(zkb: Zkb): org.red.zkb4s.CommonSchemas.ZkbMetaData = {
    org.red.zkb4s.CommonSchemas.ZkbMetaData(
      hash = zkb.hash,
      totalValue = zkb.totalValue,
      points = zkb.points)
  }

  implicit def converter(killPackage: KillPackage): org.red.zkb4s.CommonSchemas.Killmail = {
    org.red.zkb4s.CommonSchemas.Killmail(
      killId = killPackage.killID,
      killTime = string2Date(killPackage.killmail.killTime),
      victim = killPackage.killmail.victim,
      attackers = killPackage.killmail.attackers.map(attacker2attacker),
      solarSystem = killPackage.killmail.solarSystem,
      position = killPackage.killmail.victim.position,
      zkbMetadata = killPackage.zkb
    )
  }
}