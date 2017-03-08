/*package org.red.zkb4s.zkb

import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.red.zkb4s.schema.StatsSchema

import scala.annotation.tailrec
import scala.concurrent.duration._
import scalaz.concurrent.Task


class ZKBAPI(baseurl: String = "https://zkillboard.com/",
             useragent: String = "pizza-zkbapi, unknown application",
             redisqurl: String = "https://redisq.zkillboard.com/listen.php",
             strict: Boolean = true) {

  def query() = new ZKBRequest(Uri.unsafeFromString(this.baseurl + "api/"), this.useragent)

  object autocomplete {

    object Filters extends Enumeration {
      type Filters = Value
      val regionID      = Value("regionID")
      val solarSystemID = Value("solarSystemID")
      val allianceID    = Value("allianceID")
      val corporationID = Value("corporationID")
      val characterID   = Value("characterID")
      val typeID        = Value("typeID")
    }

    case class AutocompleteResult(
        id: Long,
        name: String,
        `type`: String,
        image: String
    )

    def apply(f: Filters.Filters = Filters.typeID, s: String): Task[List[AutocompleteResult]] = {
      val fullurl = s"${baseurl}autocomplete/${f.toString}/$s"
      val req = Request(uri = Uri.fromString(fullurl).toOption.get, method = Method.GET)
        .putHeaders(Header("User-Agent", useragent))

      implicit val jdec = jsonOf[List[AutocompleteResult]]

      c.fetchAs[List[AutocompleteResult]](req)
    }
  }


  object stats {
    def alliance(id: Long) = {
      val fullurl = baseurl + "api/stats/allianceID/%d/".format(id)

      val req = Request(uri = Uri.fromString(fullurl).toOption.get).putHeaders(Header("User-Agent", useragent))

      implicit val jdec = jsonOf[StatsSchema.AllianceInfo]

      c.fetchAs[StatsSchema.AllianceInfo](req)
    }
    def corporation(id: Long) = {
      val fullurl = baseurl + "api/stats/corporationID/%d/".format(id)
      val req     = Request(uri = Uri.fromString(fullurl).toOption.get).putHeaders(Header("User-Agent", useragent))

      implicit val jdec = jsonOf[StatsSchema.CorporationInfo]

      c.fetchAs[StatsSchema.CorporationInfo](req)
    }
    def character(id: Long) = {
      val fullurl = baseurl + "api/stats/characterID/%d/".format(id)
      val req     = Request(uri = Uri.fromString(fullurl).toOption.get).putHeaders(Header("User-Agent", useragent))

      implicit val jdec = jsonOf[StatsSchema.CharacterInfo]

      c.fetchAs[StatsSchema.CharacterInfo](req)
    }
  }

}
*/