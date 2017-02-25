package org.red.zkb4s.zkb

/**
  * Created by Andi on 20/01/2016.
  */
object StatsTypes {

  case class Group(
      groupID: Double,
      shipsLost: Option[Double],
      pointsLost: Option[Double],
      iskLost: Option[Double],
      shipsDestroyed: Double,
      pointsDestroyed: Double,
      iskDestroyed: Double
  )

  case class Month(
      year: Double,
      month: Double,
      shipsLost: Option[Double],
      pointsLost: Option[Double],
      iskLost: Option[Double],
      shipsDestroyed: Option[Double],
      pointsDestroyed: Option[Double],
      iskDestroyed: Option[Double]
  )

  case class SuperPilot(
      kills: Long,
      characterID: Long,
      characterName: String
  )

  case class Characters(
      `type`: String,
      count: Long
  )

  case class Activepvp(
      characters: Option[Characters],
      corporations: Option[Characters],
      ships: Option[Characters],
      systems: Option[Characters],
      regions: Option[Characters],
      kills: Option[Characters]
  )

  case class LastApiUpdate(
      sec: Double,
      usec: Double
  )

  case class AlliInfo(
      corpCount: Double,
      deleted: Boolean,
      executorCorpID: Double,
      factionID: Double,
      id: Double,
      killID: Double,
      lastApiUpdate: LastApiUpdate,
      memberCount: Double,
      name: String,
      ticker: String,
      `type`: String
  )

  case class CorpInfo(
      allianceID: Double,
      ceoID: Double,
      factionID: Double,
      id: Double,
      killID: Double,
      lastApiUpdate: LastApiUpdate,
      memberCount: Double,
      name: String,
      ticker: String,
      `type`: String
  )

  case class SuperPilots(
      data: Option[List[SuperPilot]],
      title: String
  )

  case class Supers(
      titans: SuperPilots,
      supercarriers: SuperPilots
  )

  case class AllianceInfo(
      allTimeSum: Double,
      groups: Map[Long, Group],
      id: Long,
      iskDestroyed: Double,
      iskLost: Double,
      months: Map[Long, Month],
      pointsDestroyed: Double,
      pointsLost: Double,
      sequence: Double,
      shipsDestroyed: Double,
      shipsLost: Double,
      `type`: String,
      info: AlliInfo,
      supers: Supers,
      hasSupers: Boolean
  ) {
    def getSupers: List[SuperPilot] = List(supers.supercarriers.data).flatten.flatten
    def getTitans: List[SuperPilot] = List(supers.titans.data).flatten.flatten
  }

  case class CorporationInfo(
      allTimeSum: Double,
      groups: Map[Long, Group],
      id: Long,
      iskDestroyed: Double,
      iskLost: Double,
      months: Map[Long, Month],
      pointsDestroyed: Double,
      pointsLost: Double,
      sequence: Double,
      shipsDestroyed: Double,
      shipsLost: Double,
      `type`: String,
      activepvp: Option[Activepvp],
      info: CorpInfo,
      supers: Supers,
      hasSupers: Boolean
  ) {
    def getSupers: List[SuperPilot] = List(supers.supercarriers.data).flatten.flatten
    def getTitans: List[SuperPilot] = List(supers.titans.data).flatten.flatten
  }

  case class CharInfo(
      allianceID: Long,
      corporationID: Long,
      factionID: Long,
      id: Long,
      killID: Long,
      lastApiUpdate: LastApiUpdate,
      name: String,
      `type`: String
  )

  case class CharacterInfo(
      `type`: String,
      id: Long,
      shipsLost: Long,
      pointsLost: Double,
      iskLost: Double,
      shipsDestroyed: Long,
      pointsDestroyed: Double,
      iskDestroyed: Double,
      sequence: Long,
      activepvp: Activepvp,
      info: CharInfo
      //groups: Map[Long, Group],
      //months: Map[Long, Month]
  )

}
