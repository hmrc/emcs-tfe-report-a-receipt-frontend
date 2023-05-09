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
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import models.{ListItemWithProductCode, NormalMode}
import pages.unsatisfactory.individualItems.{CheckAnswersItemPage, SelectItemsPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.GetCnCodeInformationService
import viewmodels.AddedItemsSummary
import views.html.AddedItemsView

import scala.concurrent.Future

class AddedItemsControllerSpec extends SpecBase with MockGetCnCodeInformationService {

  lazy val form = new AddAnotherItemFormProvider()()
  lazy val itemListSummary = new AddedItemsSummary()

  lazy val url = "testurl"

  "AddedItems Controller" - {

    ".onPageLoad()" - {

      "must redirect to the SelectItems page" - {

        "when no items have been added" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
            .build()

          running(application) {
            val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
          }
        }

        "when items have been added, but not completed" in {

          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
            .build()

          running(application) {
            val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
          }
        }
      }

      "when items have been added, but not all" - {

        "must render the view with the Radio option form to add more" in {

          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
            .build()

          val serviceResponse: Seq[(ListItemWithProductCode, CnCodeInformation)] =
            Seq((ListItemWithProductCode(productCode = item1.productCode, cnCode = item1.cnCode, changeUrl = url, removeUrl = url), CnCodeInformation("", `1`)))

          MockGetCnCodeInformationService.getCnCodeInformationWithListItems(Seq(
            ListItemWithProductCode(productCode = item1.productCode, cnCode = item1.cnCode, changeUrl = url, removeUrl = url)
          )).returns(Future.successful(serviceResponse))

          running(application) {
            val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
            val view = application.injector.instanceOf[AddedItemsView]
            implicit val req = dataRequest(request, userAnswers)

            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(
              form = Some(form),
              items = serviceResponse,
              action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
            ).toString
          }
        }
      }

      "when all items have been added" - {

        "must render the view without the Radio option form" in {

          val userAnswers =
            emptyUserAnswers
              .set(SelectItemsPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)
              .set(SelectItemsPage(2), item2.itemUniqueReference)
              .set(CheckAnswersItemPage(2), true)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
            .build()

          val serviceResponse: Seq[(ListItemWithProductCode, CnCodeInformation)] =
            Seq(
              (ListItemWithProductCode(productCode = item1.productCode, cnCode = item1.cnCode, changeUrl = url, removeUrl = url), CnCodeInformation("", `1`)),
              (ListItemWithProductCode(productCode = item2.productCode, cnCode = item2.cnCode, changeUrl = url, removeUrl = url), CnCodeInformation("", `1`))
            )

          MockGetCnCodeInformationService.getCnCodeInformationWithListItems(Seq(
            ListItemWithProductCode(productCode = item1.productCode, cnCode = item1.cnCode, changeUrl = url, removeUrl = url),
            ListItemWithProductCode(productCode = item2.productCode, cnCode = item2.cnCode, changeUrl = url, removeUrl = url),
          )).returns(Future.successful(serviceResponse))

          running(application) {
            val request = FakeRequest(GET, routes.AddedItemsController.onPageLoad(testErn, testArc).url)
            val view = application.injector.instanceOf[AddedItemsView]
            implicit val req = dataRequest(request, userAnswers)

            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(
              form = None,
              items = serviceResponse,
              action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
            ).toString
          }
        }
      }
    }

    ".onSubmit()" - {

      "when no items have been added" - {

        "must redirect to the SelectItems page" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
            .build()

          running(application) {
            val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
          }
        }
      }

      "when items have been added (but not all items)" - {

        "when a radio button is not selected" - {

          "must render a BAD_REQUEST with the formError" in {

            val userAnswers = emptyUserAnswers
              .set(SelectItemsPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
              .build()

            val serviceResponse: Seq[(ListItemWithProductCode, CnCodeInformation)] =
              Seq((ListItemWithProductCode(productCode = item1.productCode, cnCode = item1.cnCode, changeUrl = url, removeUrl = url), CnCodeInformation("", `1`)))

            MockGetCnCodeInformationService.getCnCodeInformationWithListItems(Seq(
              ListItemWithProductCode(productCode = item1.productCode, cnCode = item1.cnCode, changeUrl = url, removeUrl = url)
            )).returns(Future.successful(serviceResponse))

            running(application) {
              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
              val result = route(application, request).value
              val view = application.injector.instanceOf[AddedItemsView]
              val boundForm = form.bind(Map("value" -> ""))

              implicit val req = dataRequest(request, userAnswers)

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual view(
                form = Some(boundForm),
                items = serviceResponse,
                action = routes.AddedItemsController.onSubmit(testErn, testArc))(req, messages(application)
              ).toString
            }
          }
        }

        "when true is selected to add another item" - {

          "must redirect to the SelectItem view" in {

            val userAnswers = emptyUserAnswers
              .set(SelectItemsPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)

            val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

            running(application) {

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
                .withFormUrlEncodedBody("value" -> "true")

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.SelectItemsController.onPageLoad(testErn, testArc).url)
            }
          }
        }

        "when false is selected to NOT add another item" - {

          "must redirect to the AddMoreInfo view" in {

            val userAnswers = emptyUserAnswers
              .set(SelectItemsPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)
            val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

            running(application) {

              val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)
                .withFormUrlEncodedBody("value" -> "false")

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url)
            }
          }
        }
      }

      "when all items have been added" - {

        "must redirect to the AddMoreInfo view" in {

          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemsPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          running(application) {

            val request = FakeRequest(POST, routes.AddedItemsController.onSubmit(testErn, testArc).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url)
          }
        }
      }
    }
  }
}
