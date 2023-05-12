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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import models.WrongWithMovement.Damaged
import models.response.emcsTfe.MovementItem
import pages.unsatisfactory.{ExcessInformationPage, HowMuchIsWrongPage, WrongWithMovementPage}
import pages.{DateOfArrivalPage, MoreInformationPage, QuestionPage}
import pages.unsatisfactory.individualItems._
import play.api.libs.json._

import java.time.LocalDate


class UserAnswersSpec extends SpecBase {

  case class TestPage(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override def path: JsPath = jsPath \ toString
    override def toString: String = "TestPage"
  }

  case class TestPage2(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override def path: JsPath = jsPath \ toString

    override def toString: String = "TestPage2"
  }

  case class TestModel(TestPage: String,
                       TestPage2: Option[String] = None)

  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  "UserAnswers" - {

    "when calling .set(page)" - {

      "when no data exists for that page" - {

        "must set the answer for the first time" in {
          emptyUserAnswers.set(TestPage(), "foo") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
        }
      }

      "when data exists for that page" - {

        "must change the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.set(TestPage(), "bar") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "bar"
          ))
        }
      }

      "when setting at a subPath with indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 2), "wizz")


          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          )
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 2), "wizz")

          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          )
        }
      }
    }

    "when calling .get(page)" - {

      "when no data exists for that page" - {

        "must return None" in {
          emptyUserAnswers.get(TestPage()) mustBe None
        }
      }

      "when data exists for that page" - {

        "must Some(data)" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.get(TestPage()) mustBe Some("foo")
        }
      }

      "when getting data at a subPath with indexes" - {

        "must return the answer at the subPath" in {

          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0)) mustBe Some("foo")
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0 \ "subItems" \ 0)) mustBe Some("foo")
        }
      }
    }

    "when calling .remove(page)" - {

      "when no data exists for that page" - {

        "must return the emptyUserAnswers unchanged" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "AnotherPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe withData
        }
      }

      "when data exists for that page" - {

        "must remove the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe emptyUserAnswers
        }
      }

      "when removing data at a subPath with indexes" - {

        "when the page is the last page in the subObject" - {

          "must remove the entire object from the array at the subPath" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "bar"),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }

        "when the page is NOT the last page in the subObject" - {

          "must remove just that page object key from the object in the array" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj(
                  "TestPage" -> "bar",
                  "TestPage2" -> "bar2"
                ),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage2" -> "bar2"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }
      }

      "when removing at a subPath which contains nested indexes" - {

        "when the page is that last item in the arrays object" - {

          "must remove the object containing the answer from the array at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "bar"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }

        "when the page is NOT the last item in the arrays object" - {

          "must remove just that key from the object at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage" -> "bar",
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }
      }
    }

    "when calling .handleResult" - {

      "when failed to update the UserAnswers" - {

        "must throw the exception" in {
          intercept[JsResultException](emptyUserAnswers.handleResult(JsError("OhNo")))
        }
      }

      "when updated UserAnswers successfully" - {

        "must return the user answers" in {
          emptyUserAnswers.handleResult(JsSuccess(emptyUserAnswers.data)) mustBe emptyUserAnswers
        }
      }
    }

    "when calling .itemReferences" - {

      "must return all the item references" - {

        "when item references are in user answers, and it must be sorted by reference" in {

          val withData = emptyUserAnswers
            .set(SelectItemsPage(2), 2)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)
            .set(SelectItemsPage(1), 1)
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)

          withData.itemReferences mustBe Seq(1, 2)
        }
      }

      "must return a filtered list" - {

        "when not all item references are in user answers" in {

          val withData = emptyUserAnswers
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)
            .set(SelectItemsPage(2), 2)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)

          withData.itemReferences mustBe Seq(2)
        }

        "when no item references are in user answers" in {

          val withData = emptyUserAnswers
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)

          withData.itemReferences mustBe Seq()
        }

        "when user answers is empty" in {

          val withData = emptyUserAnswers

          withData.itemReferences mustBe Seq()
        }
      }
    }

    "when calling .items" - {

      "must return all the items" - {

        "when item references are in user answers, and it must be sorted by reference" in {

          val withData = emptyUserAnswers
            .set(SelectItemsPage(2), 2)
            .set(SelectItemsPage(1), 1)
            .set(CheckAnswersItemPage(1), true)
            .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(WrongWithMovement.Damaged, 3, Some("info")))

          withData.items mustBe Seq(
            ItemModel(1, Some(true), Some(ItemShortageOrExcessModel(WrongWithMovement.Damaged, 3, Some("info")))),
            ItemModel(2, None, None)
          )
        }
      }

      "must return a filtered list" - {

        "when not all item references are in user answers" in {

          val withData = emptyUserAnswers
            .set(SelectItemsPage(2), 2)
            .set(CheckAnswersItemPage(1), true)
            .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(WrongWithMovement.Damaged, 3, Some("info")))

          withData.items mustBe Seq(ItemModel(2, None, None))
        }

        "when no item references are in user answers" in {

          val withData = emptyUserAnswers
            .set(CheckAnswersItemPage(1), true)
            .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(WrongWithMovement.Damaged, 3, Some("info")))

          withData.items mustBe Seq()
        }

        "when user answers is empty" in {

          val withData = emptyUserAnswers

          withData.items mustBe Seq()
        }
      }
    }

    "when calling .itemKeys" - {
      "must return all keys" - {
        "when items is an object" in {
          val input = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.obj(
              "key1" -> "value1",
              "key2" -> "value2"
            )
          ))

          input.itemKeys mustBe Seq("key1", "key2")
        }
      }

      "must return an empty Seq" - {
        "when items is not an object" in {
          val input = emptyUserAnswers.copy(data = Json.obj(
            "items" -> JsArray(Seq(Json.obj(
              "key1" -> "value1",
              "key2" -> "value2"
            )))
          ))

          input.itemKeys mustBe Seq()
        }
      }
    }

    "when calling .getItemWithReads(key)" - {
      "must return a value" - {
        "when value found matches the Reads" in {
          val input = emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(SelectItemsPage(2), 2)

          input.getItemWithReads("item-1")(MovementItem.readItemUniqueReference) mustBe Seq(1)
        }
      }

      "must return an empty Seq" - {
        "when value found doesn't match the Reads" in {
          val input = emptyUserAnswers
            .set(CheckAnswersItemPage(1), false)
            .set(SelectItemsPage(2), 2)

          input.getItemWithReads("item-1")(ItemModel.reads) mustBe Seq()
        }

        "when no value is found for the inputted key" in {
          val input = emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(SelectItemsPage(2), 2)

          input.getItemWithReads("item-3")(MovementItem.readItemUniqueReference) mustBe Seq()
        }
      }
    }

    "when calling .filterForPages" - {
      val baseUserAnswers = UserAnswers(internalId = "my id", ern = "my ern", arc = "my arc")
      "must only return pages in the supplied Seq" in {
        val existingUserAnswers = baseUserAnswers
          .set(DateOfArrivalPage, LocalDate.MIN)
          .set(HowMuchIsWrongPage, HowMuchIsWrong.TheWholeMovement)
          .set(MoreInformationPage, Some("more info"))

        existingUserAnswers.filterForPages(Seq(DateOfArrivalPage, MoreInformationPage)) mustBe {
          baseUserAnswers.copy(data = Json.obj(
            DateOfArrivalPage.toString -> LocalDate.MIN,
            MoreInformationPage.toString -> "more info"
          ))
        }
      }

      "must return an empty Seq if none of the supplied pages are in UserAnswers" in {
        val existingUserAnswers = baseUserAnswers
          .set(DateOfArrivalPage, LocalDate.MIN)
          .set(HowMuchIsWrongPage, HowMuchIsWrong.TheWholeMovement)
          .set(MoreInformationPage, Some("more info"))

        existingUserAnswers.filterForPages(Seq(ExcessInformationPage)) mustBe baseUserAnswers
      }
    }
  }
}