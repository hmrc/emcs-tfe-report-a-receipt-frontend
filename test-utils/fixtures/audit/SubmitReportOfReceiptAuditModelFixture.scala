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

package fixtures.audit

import models.AcceptMovement._
import models.DestinationType.DirectDelivery
import models.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other}
import models.submitReportOfReceipt.{AddressModel, ReceiptedItemsModel, SubmitReportOfReceiptModel, TraderModel, UnsatisfactoryModel}
import play.api.libs.json.{JsValue, Json}
import models.audit.SubmitReportOfReceiptAuditModel

import java.time.LocalDate

object SubmitReportOfReceiptAuditModelFixture {
  val time = LocalDate.now().toString

  val submitRORAuditModelSatisfactoryModel: SubmitReportOfReceiptAuditModel =
    SubmitReportOfReceiptAuditModel(
      credentialId = "credId",
      internalId = "internalId",
      correlationId = "ABCD1234",
      ern = "ERN",
      submission = SubmitReportOfReceiptModel(
        arc = "ARC",
        sequenceNumber = 1,
        destinationType = DirectDelivery,
        consigneeTrader = Some(TraderModel(
          traderExciseNumber = Some("id"),
          traderName = Some("name"),
          address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
          eoriNumber = Some("eori")
        )),
        deliveryPlaceTrader = Some(TraderModel(
          traderExciseNumber = Some("id"),
          traderName = Some("name"),
          address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
          eoriNumber = None
        )),
        destinationOffice = "GB000434",
        dateOfArrival = LocalDate.now(),
        acceptMovement = Satisfactory,
        individualItems = Seq(),
        otherInformation = Some("other")
      )
    )

  val submitRORAuditModelUnSatisfactoryModel: SubmitReportOfReceiptAuditModel =
    SubmitReportOfReceiptAuditModel(
      credentialId = "credId",
      internalId = "internalId",
      correlationId = "ABCD1234",
      ern = "ERN",
      submission = SubmitReportOfReceiptModel(
        arc = "ARC",
        sequenceNumber = 1,
        destinationType = DirectDelivery,
        consigneeTrader = Some(TraderModel(
          traderExciseNumber = Some("id"),
          traderName = Some("name"),
          address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
          eoriNumber = Some("eori")
        )),
        deliveryPlaceTrader = Some(TraderModel(
          traderExciseNumber = Some("id"),
          traderName = Some("name"),
          address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
          eoriNumber = None
        )),
        destinationOffice = "GB000434",
        dateOfArrival = LocalDate.now(),
        acceptMovement = Unsatisfactory,
        individualItems = Seq(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            productCode = "W300",
            excessAmount = Some(12.145),
            shortageAmount = None,
            refusedAmount = None,
            unsatisfactoryReasons = Seq(UnsatisfactoryModel(reason = Excess, additionalInformation = Some("info")))
          ),
          ReceiptedItemsModel(
            eadBodyUniqueReference = 2,
            productCode = "W200",
            excessAmount = None,
            shortageAmount = Some(56),
            refusedAmount = None,
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(
                reason = Excess, additionalInformation = Some("info")
              ),
              UnsatisfactoryModel(
                reason = BrokenSeals, additionalInformation = Some("info")
              ),
              UnsatisfactoryModel(
                reason = Other, additionalInformation = Some("info")
              ),
              UnsatisfactoryModel(
                reason = Damaged, additionalInformation = Some("info")
              ),
            )
          )
        ),
        otherInformation = Some("other")
      )
    )

  val submitRORAuditModelPartiallyRefusedModel: SubmitReportOfReceiptAuditModel =
    SubmitReportOfReceiptAuditModel(
      credentialId = "credId",
      internalId = "internalId",
      correlationId = "ABCD1234",
      ern = "ERN",
      submission = SubmitReportOfReceiptModel(
      arc = "ARC",
      sequenceNumber = 1,
        destinationType = DirectDelivery,
      consigneeTrader = Some(TraderModel(
        traderExciseNumber = Some("id"),
        traderName = Some("name"),
        address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
        eoriNumber = Some("eori")
      )),
      deliveryPlaceTrader = Some(TraderModel(
        traderExciseNumber = Some("id"),
        traderName = Some("name"),
        address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
        eoriNumber = None
      )),
      destinationOffice = "GB000434",
      dateOfArrival = LocalDate.now(),
      acceptMovement = PartiallyRefused,
      individualItems = Seq(
        ReceiptedItemsModel(
          eadBodyUniqueReference = 1,
          productCode = "W300",
          excessAmount = Some(12.145),
          shortageAmount = None,
          refusedAmount = None,
          unsatisfactoryReasons = Seq(UnsatisfactoryModel(reason = Excess, additionalInformation = Some("info")))
        ),
        ReceiptedItemsModel(
          eadBodyUniqueReference = 2,
          productCode = "W200",
          excessAmount = None,
          shortageAmount = Some(56),
          refusedAmount = None,
          unsatisfactoryReasons = Seq(
            UnsatisfactoryModel(
              reason = Excess, additionalInformation = Some("info")
            ),
            UnsatisfactoryModel(
              reason = BrokenSeals, additionalInformation = Some("info")
            ),
            UnsatisfactoryModel(
              reason = Other, additionalInformation = Some("info")
            ),
            UnsatisfactoryModel(
              reason = Damaged, additionalInformation = Some("info")
            ),
          )
        )
      ),
      otherInformation = Some("other")
      )
    )

  val submitRORAuditModelRefusedModel: SubmitReportOfReceiptAuditModel =
    SubmitReportOfReceiptAuditModel(
      credentialId = "credId",
      internalId = "internalId",
      correlationId = "ABCD1234",
      ern = "ERN",
      submission = SubmitReportOfReceiptModel(
        arc = "ARC",
        sequenceNumber = 1,
        destinationType = DirectDelivery,
        consigneeTrader = Some(TraderModel(
          traderExciseNumber = Some("id"),
          traderName = Some("name"),
          address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
          eoriNumber = Some("eori")
        )),
        deliveryPlaceTrader = Some(TraderModel(
          traderExciseNumber = Some("id"),
          traderName = Some("name"),
          address = Some(AddressModel(Some("number"), Some("street"), Some("postcode"),Some("city"))),
          eoriNumber = None
        )),
        destinationOffice = "GB000434",
        dateOfArrival = LocalDate.now(),
        acceptMovement = Refused,
        individualItems = Seq(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            productCode = "W300",
            excessAmount = Some(12.145),
            shortageAmount = None,
            refusedAmount = None,
            unsatisfactoryReasons = Seq(UnsatisfactoryModel(reason = Excess, additionalInformation = Some("info")))
          ),
          ReceiptedItemsModel(
            eadBodyUniqueReference = 2,
            productCode = "W200",
            excessAmount = None,
            shortageAmount = Some(56),
            refusedAmount = None,
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(
                reason = Excess, additionalInformation = Some("info")
              ),
              UnsatisfactoryModel(
                reason = BrokenSeals, additionalInformation = Some("info")
              ),
              UnsatisfactoryModel(
                reason = Other, additionalInformation = Some("info")
              ),
              UnsatisfactoryModel(
                reason = Damaged, additionalInformation = Some("info")
              ),
            )
          )
        ),
        otherInformation = Some("other")
      )
    )

  val submitRORAuditModelSatisfactoryJson : JsValue = Json.parse(
      s"""
        |{
        |   "credentialId": "credId",
        |   "internalId": "internalId",
        |		"correlationId": "ABCD1234",
        |		"ern": "ERN",
        |		"arc": "ARC",
        |		"sequenceNumber": 1,
        |		"consigneeTrader": {
        |			"traderId": "id",
        |			"traderName": "name",
        |			"address": {
        |				"streetNumber": "number",
        |				"street": "street",
        |				"postcode": "postcode",
        |				"city": "city"
        |			},
        |			"eoriNumber": "eori"
        |		},
        |		"deliveryPlaceTrader": {
        |			"traderId": "id",
        |			"traderName": "name",
        |			"address": {
        |				"streetNumber": "number",
        |				"street": "street",
        |				"postcode": "postcode",
        |				"city": "city"
        |			}
        |		},
        |		"destinationOffice": "GB000434",
        |		"dateOfArrival": "$time",
        |		"acceptMovement": "accepted",
        |		"otherInformation": "other"
        |}
        |""".stripMargin)

  val submitRORAuditModelUnSatisfactoryJson : JsValue = Json.parse(
    s"""
      |{
      |   "credentialId": "credId",
      |   "internalId": "internalId",
      |		"correlationId": "ABCD1234",
      |		"ern": "ERN",
      |		"arc": "ARC",
      |		"sequenceNumber": 1,
      |		"consigneeTrader": {
      |			"traderId": "id",
      |			"traderName": "name",
      |			"address": {
      |				"streetNumber": "number",
      |				"street": "street",
      |				"postcode": "postcode",
      |				"city": "city"
      |			},
      |			"eoriNumber": "eori"
      |		},
      |		"deliveryPlaceTrader": {
      |			"traderId": "id",
      |			"traderName": "name",
      |			"address": {
      |				"streetNumber": "number",
      |				"street": "street",
      |				"postcode": "postcode",
      |				"city": "city"
      |			}
      |		},
      |		"destinationOffice": "GB000434",
      |		"dateOfArrival": "$time",
      |		"acceptMovement": "unsatisfactory",
      |		"individualItems": [{
      |				"eadBodyUniqueReference": 1,
      |				"productCode": "W300",
      |				"excessAmount": 12.145,
      |				"unsatisfactoryReasons": [{
      |					"reason": "excess",
      |					"additionalInformation": "info"
      |				}]
      |			},
      |			{
      |				"eadBodyUniqueReference": 2,
      |				"productCode": "W200",
      |				"shortageAmount": 56,
      |				"unsatisfactoryReasons": [{
      |						"reason": "excess",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "brokenSeals",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "other",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "damaged",
      |						"additionalInformation": "info"
      |					}
      |				]
      |			}
      |		],
      |		"otherInformation": "other"
      |}
      |""".stripMargin)

  val submitRORAuditModelPartiallyRefusedJson : JsValue = Json.parse(
    s"""
      |{
      |   "credentialId": "credId",
      |   "internalId": "internalId",
      |		"correlationId": "ABCD1234",
      |		"ern": "ERN",
      |		"arc": "ARC",
      |		"sequenceNumber": 1,
      |		"consigneeTrader": {
      |			"traderId": "id",
      |			"traderName": "name",
      |			"address": {
      |				"streetNumber": "number",
      |				"street": "street",
      |				"postcode": "postcode",
      |				"city": "city"
      |			},
      |			"eoriNumber": "eori"
      |		},
      |		"deliveryPlaceTrader": {
      |			"traderId": "id",
      |			"traderName": "name",
      |			"address": {
      |				"streetNumber": "number",
      |				"street": "street",
      |				"postcode": "postcode",
      |				"city": "city"
      |			}
      |		},
      |		"destinationOffice": "GB000434",
      |		"dateOfArrival": "$time",
      |		"acceptMovement": "partiallyRefused",
      |		"individualItems": [{
      |				"eadBodyUniqueReference": 1,
      |				"productCode": "W300",
      |				"excessAmount": 12.145,
      |				"unsatisfactoryReasons": [{
      |					"reason": "excess",
      |					"additionalInformation": "info"
      |				}]
      |			},
      |			{
      |				"eadBodyUniqueReference": 2,
      |				"productCode": "W200",
      |				"shortageAmount": 56,
      |				"unsatisfactoryReasons": [{
      |						"reason": "excess",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "brokenSeals",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "other",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "damaged",
      |						"additionalInformation": "info"
      |					}
      |				]
      |			}
      |		],
      |		"otherInformation": "other"
      |}
      |""".stripMargin)

  val submitRORAuditModelRefusedJson : JsValue = Json.parse(
    s"""
      |{
      |   "credentialId": "credId",
      |   "internalId": "internalId",
      |		"correlationId": "ABCD1234",
      |		"ern": "ERN",
      |		"arc": "ARC",
      |		"sequenceNumber": 1,
      |		"consigneeTrader": {
      |			"traderId": "id",
      |			"traderName": "name",
      |			"address": {
      |				"streetNumber": "number",
      |				"street": "street",
      |				"postcode": "postcode",
      |				"city": "city"
      |			},
      |			"eoriNumber": "eori"
      |		},
      |		"deliveryPlaceTrader": {
      |			"traderId": "id",
      |			"traderName": "name",
      |			"address": {
      |				"streetNumber": "number",
      |				"street": "street",
      |				"postcode": "postcode",
      |				"city": "city"
      |			}
      |		},
      |		"destinationOffice": "GB000434",
      |		"dateOfArrival": "$time",
      |		"acceptMovement": "refused",
      |		"individualItems": [{
      |				"eadBodyUniqueReference": 1,
      |				"productCode": "W300",
      |				"excessAmount": 12.145,
      |				"unsatisfactoryReasons": [{
      |					"reason": "excess",
      |					"additionalInformation": "info"
      |				}]
      |			},
      |			{
      |				"eadBodyUniqueReference": 2,
      |				"productCode": "W200",
      |				"shortageAmount": 56,
      |				"unsatisfactoryReasons": [{
      |						"reason": "excess",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "brokenSeals",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "other",
      |						"additionalInformation": "info"
      |					},
      |					{
      |						"reason": "damaged",
      |						"additionalInformation": "info"
      |					}
      |				]
      |			}
      |		],
      |		"otherInformation": "other"
      |	}
      |""".stripMargin)
}
