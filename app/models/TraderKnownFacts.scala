package models

import play.api.libs.json.{Json, OFormat}

case class TraderKnownFacts(
                             traderName: String,
                             addressLine1: Option[String],
                             addressLine2: Option[String],
                             addressLine3: Option[String],
                             addressLine4: Option[String],
                             addressLine5: Option[String],
                             postcode: Option[String]
                           )

object TraderKnownFacts {
  implicit val fmt: OFormat[TraderKnownFacts] = Json.format
}
