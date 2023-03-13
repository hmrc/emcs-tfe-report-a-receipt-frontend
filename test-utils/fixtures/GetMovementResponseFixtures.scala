/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fixtures

import models.response.emcsTfe.{GetMovementResponse, MovementItem, Packaging}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

trait GetMovementResponseFixtures { _: BaseFixtures =>

  val boxPackage = Packaging(
    typeOfPackage = "BX",
    quantity = 165
  )

  val cratePackage = Packaging(
    typeOfPackage = "CR",
    quantity = 12
  )

  val item1 = MovementItem(
    itemUniqueReference = 1,
    productCode = "W200",
    cnCode = "22041011",
    quantity = BigDecimal(500),
    grossMass = BigDecimal(900),
    netMass = BigDecimal(375),
    alcoholicStrength = Some(BigDecimal(12.7)),
    packaging = Seq(boxPackage)
  )

  val item2 = MovementItem(
    itemUniqueReference = 2,
    productCode = "W300",
    cnCode = "22041011",
    quantity = BigDecimal(550),
    grossMass = BigDecimal(910),
    netMass = BigDecimal(315),
    alcoholicStrength = None,
    packaging = Seq(boxPackage, cratePackage)
  )

  val getMovementResponseModel: GetMovementResponse = GetMovementResponse(
    localReferenceNumber = "MyLrn",
    eadStatus = "MyEadStatus",
    consignorName = "MyConsignor",
    dateOfDispatch = LocalDate.parse("2010-03-04"),
    journeyTime = "MyJourneyTime",
    items = Seq(item1),
    numberOfItems = 1
  )

  val getMovementResponseJson: JsValue = Json.obj(
      "localReferenceNumber" -> "MyLrn",
      "eadStatus" -> "MyEadStatus",
      "consignorName" -> "MyConsignor",
      "dateOfDispatch" -> "2010-03-04",
      "journeyTime" -> "MyJourneyTime",
      "items" -> Json.arr(
        Json.obj(fields =
          "itemUniqueReference" -> 1,
          "productCode" -> "W200",
          "cnCode" -> "22041011",
          "quantity" -> 500,
          "grossMass" -> 900,
          "netMass" -> 375,
          "alcoholicStrength" -> 12.7,
          "packaging" -> Json.arr(
            Json.obj(fields =
              "typeOfPackage" -> "BX",
              "quantity" -> 165
            )
          )
        )
      ),
      "numberOfItems" -> 1
  )
}
