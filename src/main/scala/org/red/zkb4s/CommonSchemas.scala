package org.red.zkb4s

import java.util.Date


object CommonSchemas {

  case class Killmail(killId: Long,
                      killTime: Date,
                      victim: Victim,
                      attackers: List[Attacker],
                      position: Position,
                      zkbMetadata: ZkbMetaData)

  case class Victim(shipId: Long,
                    character: Character,
                    items: List[Item],
                    damageTaken: Long)

  case class Attacker(shipId: Option[Long],     // None if NPC
                      character: Character,
                      weaponType: Option[Long], // None if unknown
                      damageDone: Long,
                      finalBlow: Boolean,
                      securityStatus: Double)

  case class Character(characterId: Option[Long],   // None if Entity [POS/Modules/Citadel/etc]
                       corporationId: Option[Long], // None if NPC corporation
                       allianceId: Option[Long])    // None if no alliance

  case class Item(itemId: Long,
                  quantityDestroyed: Long,
                  quantityDropped: Long)

  case class Position(x: Double, y: Double, z: Double)

  case class ZkbMetaData(hash: String, totalValue: Double, points: Long)
}
