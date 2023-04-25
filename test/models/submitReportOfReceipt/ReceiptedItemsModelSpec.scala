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
import fixtures.ReceiptedItemsModelFixtures
import models.AcceptMovement.{PartiallyRefused, Refused, Satisfactory, Unsatisfactory}
import models.HowMuchIsWrong.{IndividualItem, TheWholeMovement}
import models.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other, Shortage, ShortageOrExcess}
import models.{ItemShortageOrExcessModel, WrongWithMovement}
import pages.unsatisfactory.individualItems._
import pages.unsatisfactory._
import pages.{AcceptMovementPage, DateOfArrivalPage}
import play.api.libs.json.Json

class ReceiptedItemsModelSpec extends SpecBase with ReceiptedItemsModelFixtures {

  "ReceiptedItemsModel" - {

    "should for a shortage with all other reasons" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(shortageReceiptedItemsModel).as[ReceiptedItemsModel] mustBe shortageReceiptedItemsModel
      }
    }

    "for a excess with no other reasons" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(excessReceiptedItemsModel).as[ReceiptedItemsModel] mustBe excessReceiptedItemsModel
      }
    }

    "for the minimum amount of info" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(minReceiptedItemsModel).as[ReceiptedItemsModel] mustBe minReceiptedItemsModel
      }
    }

    "calling .apply(_: movementDetails)(_: UserAnswers)" - {

      "must construct a sequence of receipted items from UserAnswers correctly" - {

        "when the movement is satisfactory" - {

          "must return no receipted items info" in {

            val userAnswers =
              emptyUserAnswers
                .set(DateOfArrivalPage, testDateOfArrival)
                .set(AcceptMovementPage, Satisfactory)

            val items = ReceiptedItemsModel(getMovementResponseModel)(userAnswers)

            items mustBe Seq()
          }
        }

        "when the WholeMovement is unsatisfactory/refused" - {

          "must return the expected items with the same unsatisfactory reasons against each item" in {

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

            val items = ReceiptedItemsModel(getMovementResponseModel)(userAnswers)

            items mustBe Seq(
              ReceiptedItemsModel(
                eadBodyUniqueReference = item1.itemUniqueReference,
                productCode = item1.productCode,
                excessAmount = None,
                shortageAmount = None,
                refusedAmount = None,
                unsatisfactoryReasons = Seq(
                  UnsatisfactoryModel(
                    reason = BrokenSeals,
                    additionalInformation = Some("BrokenSeals")
                  ),
                  UnsatisfactoryModel(
                    reason = Damaged,
                    additionalInformation = Some("Damage")
                  ),
                  UnsatisfactoryModel(
                    reason = Excess,
                    additionalInformation = Some("Excess")
                  ),
                  UnsatisfactoryModel(
                    reason = Other,
                    additionalInformation = Some("Other")
                  ),
                  UnsatisfactoryModel(
                    reason = Shortage,
                    additionalInformation = Some("Shortage")
                  )
                )
              ),
              ReceiptedItemsModel(
                eadBodyUniqueReference = item2.itemUniqueReference,
                productCode = item2.productCode,
                excessAmount = None,
                shortageAmount = None,
                refusedAmount = None,
                unsatisfactoryReasons = Seq(
                  UnsatisfactoryModel(
                    reason = BrokenSeals,
                    additionalInformation = Some("BrokenSeals")
                  ),
                  UnsatisfactoryModel(
                    reason = Damaged,
                    additionalInformation = Some("Damage")
                  ),
                  UnsatisfactoryModel(
                    reason = Excess,
                    additionalInformation = Some("Excess")
                  ),
                  UnsatisfactoryModel(
                    reason = Other,
                    additionalInformation = Some("Other")
                  ),
                  UnsatisfactoryModel(
                    reason = Shortage,
                    additionalInformation = Some("Shortage")
                  )
                )
              )
            )
          }
        }

        "when the IndividualItems are unsatisfactory/refused" - {

          "must return specific unsatisfactory reasons for the selecterd items only" in {

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

            val items = ReceiptedItemsModel(getMovementResponseModel)(userAnswers)

            items mustBe Seq(
              ReceiptedItemsModel(
                eadBodyUniqueReference = item1.itemUniqueReference,
                productCode = item1.productCode,
                excessAmount = None,
                shortageAmount = Some(12.45),
                refusedAmount = None,
                unsatisfactoryReasons = Seq(
                  UnsatisfactoryModel(
                    reason = BrokenSeals,
                    additionalInformation = Some("BrokenSeals")
                  ),
                  UnsatisfactoryModel(
                    reason = Damaged,
                    additionalInformation = Some("Damage")
                  ),
                  UnsatisfactoryModel(
                    reason = Other,
                    additionalInformation = Some("Other")
                  ),
                  UnsatisfactoryModel(
                    reason = Shortage,
                    additionalInformation = Some("Shortage")
                  )
                )
              ),
              ReceiptedItemsModel(
                eadBodyUniqueReference = item2.itemUniqueReference,
                productCode = item2.productCode,
                excessAmount = Some(12.45),
                shortageAmount = None,
                refusedAmount = None,
                unsatisfactoryReasons = Seq(
                  UnsatisfactoryModel(
                    reason = BrokenSeals,
                    additionalInformation = Some("BrokenSeals")
                  ),
                  UnsatisfactoryModel(
                    reason = Excess,
                    additionalInformation = Some("Excess")
                  )
                )
              )
            )
          }
        }

        "when the IndividualItems are partially refused" - {

          "must return specific unsatisfactory reasons for the selected items only with a refused amount" in {

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

            val items = ReceiptedItemsModel(getMovementResponseModel)(userAnswers)

            items mustBe Seq(
              ReceiptedItemsModel(
                eadBodyUniqueReference = item1.itemUniqueReference,
                productCode = item1.productCode,
                excessAmount = None,
                shortageAmount = Some(12.45),
                refusedAmount = Some(10.99),
                unsatisfactoryReasons = Seq(
                  UnsatisfactoryModel(
                    reason = Damaged,
                    additionalInformation = Some("Damage")
                  ),
                  UnsatisfactoryModel(
                    reason = Shortage,
                    additionalInformation = Some("Shortage")
                  )
                )
              ),
              ReceiptedItemsModel(
                eadBodyUniqueReference = item2.itemUniqueReference,
                productCode = item2.productCode,
                excessAmount = None,
                shortageAmount = None,
                refusedAmount = None,
                unsatisfactoryReasons = Seq(
                  UnsatisfactoryModel(
                    reason = BrokenSeals,
                    additionalInformation = Some("BrokenSeals")
                  )
                )
              )
            )
          }
        }
      }
    }
  }
}
