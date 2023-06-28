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

package controllers

import base.SpecBase
import forms.AddAnotherItemFormProvider
import mocks.services.MockGetCnCodeInformationService
import mocks.viewmodels.{MockCheckAnswersHelper, MockCheckAnswersItemHelper}
import models.AcceptMovement.{PartiallyRefused, Refused}
import models.ReferenceDataUnitOfMeasure.`1`
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import models.{ListItemWithProductCode, NormalMode, UserAnswers}
import pages.AcceptMovementPage
import pages.unsatisfactory.individualItems.{CheckAnswersItemPage, RefusedAmountPage, SelectItemsPage}
import play.api.inject
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.GetCnCodeInformationService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.AddedItemsSummary
import viewmodels.checkAnswers.{CheckAnswersHelper, CheckAnswersItemHelper}
import views.html.AddedItemsView

import scala.concurrent.Future

class AddedItemsControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockCheckAnswersItemHelper {

  lazy val form = new AddAnotherItemFormProvider()()
  lazy val itemListSummary = new AddedItemsSummary()

  lazy val url = "testurl"

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[CheckAnswersItemHelper].toInstance(mockCheckAnswersItemHelper),
        bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService)
      )
      .build()

    lazy val view = application.injector.instanceOf[AddedItemsView]
  }

  "AddedItems Controller" - {

    ".onPageLoad()" - {

      "must redirect to the SelectItems page" - {

        "when no items have been added" in new Fixture() {

          val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
        }

        "when items have been added, but not completed" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
        )) {

          val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
        }
      }

      "when items have been added, but not all" - {

        "must render the view with the Radio option form to add more" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
        )) {

          val serviceResponse: Seq[(MovementItem, CnCodeInformation)] = Seq(item1 -> CnCodeInformation("", "", `1`))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(serviceResponse))
          MockCheckAnswersItemHelper.summaryList().returns(SummaryList())

          val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)

          implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = Some(form),
            itemList = Seq[(Int, SummaryList)](1 -> SummaryList()),
            allItemsAdded = false,
            action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
          ).toString
        }
      }

      "when all items have been added" - {

        "must render the view without the Radio option form" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemsPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2))
            .returns(Future.successful(Seq(
              item1 -> CnCodeInformation("", "", `1`),
              item2 -> CnCodeInformation("", "", `1`)
            )))
          MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

          val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
          implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

          val result = route(application, request).value

          status(result) mustEqual OK

          contentAsString(result) mustEqual view(
            form = Some(form),
            itemList = Seq[(Int, SummaryList)](1 -> SummaryList(), 2 -> SummaryList()),
            allItemsAdded = true,
            action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
          ).toString
        }
      }
    }

    ".onSubmit()" - {

      "and had answered `No, I want to refuse it`" - {

        "when no items have been added" - {

          "must redirect to the SelectItems page" in new Fixture(Some(
            emptyUserAnswers.set(AcceptMovementPage, Refused)
          )) {

            val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
          }
        }

        "when items have been added (but not all items)" - {

          "when a radio button is not selected" - {

            "must render a BAD_REQUEST with the formError" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, Refused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(CheckAnswersItemPage(1), true)
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                Seq(item1 -> CnCodeInformation("", "", `1`))
              ))
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
              val result = route(application, request).value

              val boundForm = form.bind(Map("value" -> ""))

              implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual view(
                form = Some(boundForm),
                itemList = Seq(1 -> SummaryList()),
                allItemsAdded = false,
                action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
              ).toString
            }
          }

          "when true is selected to add another item" - {

            "must redirect to the SelectItem view" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, Refused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(CheckAnswersItemPage(1), true)
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                Seq(item1 -> CnCodeInformation("", "", `1`))
              ))
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
                .withFormUrlEncodedBody("value" -> "true")

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
            }
          }

          "when false is selected to NOT add another item" - {

            "must redirect to the AddMoreInfo view" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, Refused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(RefusedAmountPage(1), BigDecimal(1))
                .set(CheckAnswersItemPage(1), true)
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                Seq(item1 -> CnCodeInformation("", "", `1`))
              ))
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
                .withFormUrlEncodedBody("value" -> "false")

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url)
            }
          }
        }

        "when all items have been added" - {

          "must redirect to the AddMoreInfo view" in new Fixture(Some(
            emptyUserAnswers
              .set(AcceptMovementPage, Refused)
              .set(SelectItemsPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)
              .set(SelectItemsPage(2), item2.itemUniqueReference)
              .set(CheckAnswersItemPage(2), true)
          )) {

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2)).returns(Future.successful(
              Seq(
                item1 -> CnCodeInformation("", "", `1`),
                item2 -> CnCodeInformation("", "", `1`)
              )
            ))
            MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

            val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url).withFormUrlEncodedBody("value" -> "false")

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url)
          }
        }
      }

      "and had answered `No, I want to partially refuse it`" - {

        "when no items have been added" - {

          "must redirect to the SelectItems page" in new Fixture(Some(
            emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)
          )) {

            val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
          }
        }

        "when items have been added (but not all items)" - {

          "when a radio button is not selected" - {

            "must render a BAD_REQUEST with the formError" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, PartiallyRefused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(CheckAnswersItemPage(1), true)
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                Seq(item1 -> CnCodeInformation("", "", `1`))
              ))
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
              val result = route(application, request).value
              val boundForm = form.bind(Map("value" -> ""))

              implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual view(
                form = Some(boundForm),
                itemList = Seq(1 -> SummaryList()),
                allItemsAdded = false,
                action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
              ).toString
            }
          }

          "when true is selected to add another item" - {

            "must redirect to the SelectItem view" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, PartiallyRefused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(CheckAnswersItemPage(1), true)
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                Seq(item1 -> CnCodeInformation("", "", `1`)))
              )
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
                .withFormUrlEncodedBody("value" -> "true")

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
            }
          }

          "when false is selected to NOT add another item" - {

            "must redirect to the AddMoreInfo view" - {

              "when at least one item has a rejected amount" in new Fixture(Some(
                emptyUserAnswers
                  .set(AcceptMovementPage, PartiallyRefused)
                  .set(SelectItemsPage(1), item1.itemUniqueReference)
                  .set(RefusedAmountPage(1), BigDecimal(1))
                  .set(CheckAnswersItemPage(1), true)
              )) {

                MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                  Seq(item1 -> CnCodeInformation("", "", `1`))
                ))
                MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

                val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
                  .withFormUrlEncodedBody("value" -> "false")

                val result = route(application, request).value

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe Some(routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url)
              }
            }

            "must show a global error" - {

              "when no items have at least some rejected amount" in new Fixture(Some(
                emptyUserAnswers
                  .set(AcceptMovementPage, PartiallyRefused)
                  .set(SelectItemsPage(1), item1.itemUniqueReference)
                  .set(CheckAnswersItemPage(1), true)
              )) {

                MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
                  Seq(item1 -> CnCodeInformation("", "", `1`))
                ))
                MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

                val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url).withFormUrlEncodedBody("value" -> "false")
                val result = route(application, request).value
                val boundForm = form.bind(Map("value" -> "false")).withGlobalError("addedItems.error.atLeastOneItem")

                implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

                status(result) mustEqual BAD_REQUEST
                contentAsString(result) mustEqual view(
                  form = Some(boundForm),
                  itemList = Seq(1 -> SummaryList()),
                  allItemsAdded = false,
                  action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
                ).toString
              }
            }
          }
        }

        "when all items have been added" - {

          "must show a global error" - {

            "when none of the items have any refused amount" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, PartiallyRefused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(CheckAnswersItemPage(1), true)
                .set(SelectItemsPage(2), item2.itemUniqueReference)
                .set(CheckAnswersItemPage(2), true)
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2)).returns(Future.successful(
                Seq(
                  item1 -> CnCodeInformation("", "", `1`),
                  item2 -> CnCodeInformation("", "", `1`)
                )
              ))
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url).withFormUrlEncodedBody("value" -> "false")
              val result = route(application, request).value
              val boundForm = form.bind(Map("value" -> "false")).withGlobalError("addedItems.error.atLeastOneItem")

              implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual view(
                form = Some(boundForm),
                itemList = Seq(1 -> SummaryList(), 2 -> SummaryList()),
                allItemsAdded = true,
                action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
              ).toString
            }
          }

          "must redirect to the AddMoreInfo view" - {

            "when at least one item, has a refused amount" in new Fixture(Some(
              emptyUserAnswers
                .set(AcceptMovementPage, PartiallyRefused)
                .set(SelectItemsPage(1), item1.itemUniqueReference)
                .set(CheckAnswersItemPage(1), true)
                .set(SelectItemsPage(2), item2.itemUniqueReference)
                .set(CheckAnswersItemPage(2), true)
                .set(RefusedAmountPage(2), BigDecimal(1))
            )) {

              MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2)).returns(Future.successful(
                Seq(
                  item1 -> CnCodeInformation("", "", `1`),
                  item2 -> CnCodeInformation("", "", `1`)
                )
              ))
              MockCheckAnswersItemHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url).withFormUrlEncodedBody("value" -> "false")

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url)
            }
          }
        }
      }
    }
  }
}
