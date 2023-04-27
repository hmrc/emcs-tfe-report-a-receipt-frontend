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

package models.submitReportOfReceipt

import base.SpecBase
import config.AppConfig
import fixtures.SubmitReportOfReceiptFixtures
import models.AcceptMovement.{PartiallyRefused, Refused, Satisfactory, Unsatisfactory}
import models.HowMuchIsWrong.{IndividualItem, TheWholeMovement}
import models.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other, Shortage, ShortageOrExcess}
import models.response.MissingMandatoryPage
import models.{ItemShortageOrExcessModel, WrongWithMovement}
import org.scalamock.scalatest.MockFactory
import pages.unsatisfactory.individualItems._
import pages.unsatisfactory._
import pages.{AcceptMovementPage, AddMoreInformationPage, DateOfArrivalPage, MoreInformationPage}
import play.api.libs.json.Json
import utils.ModelConstructorHelpers

class SubmitReportOfReceiptModelSpec extends SpecBase with SubmitReportOfReceiptFixtures with MockFactory with ModelConstructorHelpers {

  lazy val mockAppConfig = mock[AppConfig]

  "SubmitReportOfReceiptModel" - {

    "must for the maximum number of fields" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(maxSubmitReportOfReceiptModel).as[SubmitReportOfReceiptModel] mustBe maxSubmitReportOfReceiptModel
      }
    }

    "must for the minimum number of fields" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(minSubmitReportOfReceiptModel).as[SubmitReportOfReceiptModel] mustBe minSubmitReportOfReceiptModel
      }
    }

    "calling .apply(_: movementDetails)(_: UserAnswers)" - {

      "must construct from UserAnswers correctly" - {

        "when the movement is satisfactory" in {

          (() => mockAppConfig.destinationOffice).expects().returns("GB004098").anyNumberOfTimes()

          val userAnswers =
            emptyUserAnswers
              .set(DateOfArrivalPage, testDateOfArrival)
              .set(AcceptMovementPage, Satisfactory)
              .set(AddMoreInformationPage, true)
              .set(MoreInformationPage, Some("ExtraInfo"))

          val submission = SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig)

          submission mustBe SubmitReportOfReceiptModel(
            arc = getMovementResponseModel.arc,
            sequenceNumber = getMovementResponseModel.sequenceNumber,
            consigneeTrader = getMovementResponseModel.consigneeTrader,
            deliveryPlaceTrader = getMovementResponseModel.deliveryPlaceTrader,
            destinationOffice = "GB004098",
            dateOfArrival = testDateOfArrival,
            acceptMovement = Satisfactory,
            individualItems = ReceiptedItemsModel(getMovementResponseModel)(userAnswers),
            otherInformation = Some("ExtraInfo")
          )
        }

        "when the WholeMovement is unsatisfactory/refused" in {

          (() => mockAppConfig.destinationOffice).expects().returns("GB004098").anyNumberOfTimes()

          val userAnswers =
            emptyUserAnswers
              .set(DateOfArrivalPage, testDateOfArrival)
              .set(AcceptMovementPage, Refused)
              .set(HowMuchIsWrongPage, TheWholeMovement)
              .set(WrongWithMovementPage, Set[WrongWithMovement](
                Shortage,
                Excess,
                Damaged,
                BrokenSeals,
                Other
              ))
              .set(ShortageInformationPage, Some("Shortage"))
              .set(ExcessInformationPage, Some("Excess"))
              .set(DamageInformationPage, Some("Damage"))
              .set(SealsInformationPage, Some("BrokenSeals"))
              .set(OtherInformationPage, "Other")
              .set(AddMoreInformationPage, true)
              .set(MoreInformationPage, Some("ExtraInfo"))

          val submission = SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig)

          submission mustBe SubmitReportOfReceiptModel(
            arc = getMovementResponseModel.arc,
            sequenceNumber = getMovementResponseModel.sequenceNumber,
            consigneeTrader = getMovementResponseModel.consigneeTrader,
            deliveryPlaceTrader = getMovementResponseModel.deliveryPlaceTrader,
            destinationOffice = "GB004098",
            dateOfArrival = testDateOfArrival,
            acceptMovement = Refused,
            individualItems = ReceiptedItemsModel(getMovementResponseModel)(userAnswers),
            otherInformation = Some("ExtraInfo")
          )
        }

        "when the IndividualItems are unsatisfactory/refused" in {

          (() => mockAppConfig.destinationOffice).expects().returns("GB004098").anyNumberOfTimes()

          val userAnswers =
            emptyUserAnswers
              .set(DateOfArrivalPage, testDateOfArrival)
              .set(AcceptMovementPage, Unsatisfactory)
              .set(HowMuchIsWrongPage, IndividualItem)
              //-----------------
              //Item 1 Starts ==>
              .set(SelectItemsPage(item1.itemUniqueReference), item1.itemUniqueReference)
              .set(WrongWithItemPage(item1.itemUniqueReference), Set[WrongWithMovement](
                ShortageOrExcess,
                Damaged,
                BrokenSeals,
                Other
              ))
              .set(ItemShortageOrExcessPage(item1.itemUniqueReference), ItemShortageOrExcessModel(
                wrongWithItem = Shortage,
                amount = 12.45,
                additionalInfo = Some("Shortage")
              ))
              .set(ItemDamageInformationPage(item1.itemUniqueReference), Some("Damage"))
              .set(ItemSealsInformationPage(item1.itemUniqueReference), Some("BrokenSeals"))
              .set(ItemOtherInformationPage(item1.itemUniqueReference), "Other")
              // <== Item 1 Ends
              // -----------------
              // Item 2 Starts ==>
              .set(SelectItemsPage(item2.itemUniqueReference), item2.itemUniqueReference)
              .set(WrongWithItemPage(item2.itemUniqueReference), Set[WrongWithMovement](
                ShortageOrExcess,
                BrokenSeals
              ))
              .set(ItemShortageOrExcessPage(item2.itemUniqueReference), ItemShortageOrExcessModel(
                wrongWithItem = Excess,
                amount = 12.45,
                additionalInfo = Some("Excess")
              ))
              .set(ItemSealsInformationPage(item2.itemUniqueReference), Some("BrokenSeals"))

          val submission = SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig)

          submission mustBe SubmitReportOfReceiptModel(
            arc = getMovementResponseModel.arc,
            sequenceNumber = getMovementResponseModel.sequenceNumber,
            consigneeTrader = getMovementResponseModel.consigneeTrader,
            deliveryPlaceTrader = getMovementResponseModel.deliveryPlaceTrader,
            destinationOffice = "GB004098",
            dateOfArrival = testDateOfArrival,
            acceptMovement = Unsatisfactory,
            individualItems = ReceiptedItemsModel(getMovementResponseModel)(userAnswers),
            otherInformation = None
          )
        }

        "when the IndividualItems are partially refused" in {

          (() => mockAppConfig.destinationOffice).expects().returns("GB004098").anyNumberOfTimes()

          val userAnswers =
            emptyUserAnswers
              .set(DateOfArrivalPage, testDateOfArrival)
              .set(AcceptMovementPage, PartiallyRefused)
              //-----------------
              //Item 1 Starts ==>
              .set(SelectItemsPage(item1.itemUniqueReference), item1.itemUniqueReference)
              .set(RefusingAnyAmountOfItemPage(item1.itemUniqueReference), true)
              .set(RefusedAmountPage(item1.itemUniqueReference), BigDecimal(10.99))
              .set(WrongWithItemPage(item1.itemUniqueReference), Set[WrongWithMovement](
                ShortageOrExcess,
                Damaged
              ))
              .set(ItemShortageOrExcessPage(item1.itemUniqueReference), ItemShortageOrExcessModel(
                wrongWithItem = Shortage,
                amount = 12.45,
                additionalInfo = Some("Shortage")
              ))
              .set(ItemDamageInformationPage(item1.itemUniqueReference), Some("Damage"))
              // <== Item 1 Ends
              // -----------------
              // Item 2 Starts ==>
              .set(SelectItemsPage(item2.itemUniqueReference), item2.itemUniqueReference)
              .set(WrongWithItemPage(item2.itemUniqueReference), Set[WrongWithMovement](BrokenSeals))
              .set(ItemSealsInformationPage(item2.itemUniqueReference), Some("BrokenSeals"))

          val submission = SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig)

          submission mustBe SubmitReportOfReceiptModel(
            arc = getMovementResponseModel.arc,
            sequenceNumber = getMovementResponseModel.sequenceNumber,
            consigneeTrader = getMovementResponseModel.consigneeTrader,
            deliveryPlaceTrader = getMovementResponseModel.deliveryPlaceTrader,
            destinationOffice = "GB004098",
            dateOfArrival = testDateOfArrival,
            acceptMovement = PartiallyRefused,
            individualItems = ReceiptedItemsModel(getMovementResponseModel)(userAnswers),
            otherInformation = None
          )
        }
      }

      "must throw an exception when the DateOfArrival is missing" in {

        (() => mockAppConfig.destinationOffice).expects().returns("GB004098").anyNumberOfTimes()

        val userAnswers =
          emptyUserAnswers
            .set(AcceptMovementPage, Satisfactory)

        val err = intercept[MissingMandatoryPage](SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig))

        err.getMessage mustBe s"Missing mandatory UserAnswer for page: '$DateOfArrivalPage'"
      }

      "must throw an exception when the AcceptMovement is missing" in {

        (() => mockAppConfig.destinationOffice).expects().returns("GB004098").anyNumberOfTimes()

        val userAnswers =
          emptyUserAnswers
            .set(DateOfArrivalPage, testDateOfArrival)

        val err = intercept[MissingMandatoryPage](SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig))

        err.getMessage mustBe s"Missing mandatory UserAnswer for page: '$AcceptMovementPage'"
      }
    }
  }
}
