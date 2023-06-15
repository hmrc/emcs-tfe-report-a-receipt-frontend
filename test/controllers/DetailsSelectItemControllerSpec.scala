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
import forms.DetailsSelectItemFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockGetPackagingTypesService, MockUserAnswersService}
import models.AcceptMovement.{PartiallyRefused, Unsatisfactory}
import models.NormalMode
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import pages.AcceptMovementPage
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, GetPackagingTypesService, UserAnswersService}
import views.html.DetailsSelectItemView

import scala.concurrent.Future

class DetailsSelectItemControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockUserAnswersService
  with MockGetPackagingTypesService {

  def onwardRoute = Call("GET", "/foo")

  lazy val detailsSelectItemRoute = routes.DetailsSelectItemController.onPageLoad(testErn, testArc, 1).url

  val formProvider = new DetailsSelectItemFormProvider()

  val form = formProvider()

  "DetailsSelectItemPage Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
            bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
          .build()

        MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
          item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
        )))

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq(
          (item1, CnCodeInformation("", "", `1`))
        )))

        running(application) {

          val request = FakeRequest(GET, detailsSelectItemRoute)
          val result = route(application, request).value
          val view = application.injector.instanceOf[DetailsSelectItemView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, item1, CnCodeInformation("", "", `1`))(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to /select-items" - {

        "when the item doesn't exist in UserAnswers" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
            .build()

          running(application) {

            val request = FakeRequest(GET, routes.DetailsSelectItemController.onPageLoad(testErn, testArc, 3).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }

        "when the call to get CN information returned no data" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
            .build()

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
            item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
          )))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq.empty))

          running(application) {

            val request = FakeRequest(GET, detailsSelectItemRoute)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }

      }

    }

    "when calling .onPageSubmit()" - {

      "must redirect to /select-items" - {

        "when the item doesn't exist in UserAnswers" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
            .build()

          running(application) {
            val request = FakeRequest(POST, routes.DetailsSelectItemController.onPageLoad(testErn, testArc, 3).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }

        "when the call to get CN information returned no data" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
            .build()

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
            item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
          )))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq.empty))

          running(application) {
            val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "true"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }

        "when the user answer NO to the question on the page" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
            .build()

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
            item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
          )))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq(
            (item1, CnCodeInformation("", "", `1`))
          )))

          running(application) {
            val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "false"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }
      }

      "when the user has partially refused the movement" - {

        "must redirect to /choose-refuse-item" - {

          "when the user answers YES to the question on the page" in {
            val startingAnswers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)

            val application = applicationBuilder(userAnswers = Some(startingAnswers))
              .overrides(
                bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
                bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
                bind[UserAnswersService].toInstance(mockUserAnswersService)
              )
              .build()

            MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
              item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
            )))

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq(
              (item1, CnCodeInformation("", "", `1`))
            )))

            val updatedAnswers = startingAnswers.set(SelectItemsPage(1), item1.itemUniqueReference)
            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            running(application) {
              val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "true"))
              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual routes.RefusingAnyAmountOfItemController.onPageLoad(testErn, testArc, 1, NormalMode).url
            }
          }
        }
      }

      "when the user has NOT partially refused the movement" - {

        "must redirect to /what-wrong-item" - {

          "when the user answers YES to the question on the page" in {
            val startingAnswers = emptyUserAnswers.set(AcceptMovementPage, Unsatisfactory)

            val application = applicationBuilder(userAnswers = Some(startingAnswers))
              .overrides(
                bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
                bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
                bind[UserAnswersService].toInstance(mockUserAnswersService)
              )
              .build()

            MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
              item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
            )))

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq(
              (item1, CnCodeInformation("", "", `1`))
            )))

            val updatedAnswers = startingAnswers.set(SelectItemsPage(1), item1.itemUniqueReference)
            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            running(application) {
              val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "true"))
              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual routes.WrongWithMovementController.loadWrongWithItem(testErn, testArc, 1, NormalMode).url
            }
          }
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
            bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService))
          .build()

        MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(
          item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
        )))

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq(
          (item1, CnCodeInformation("", "", `1`))
        )))

        running(application) {
          val boundForm = form.bind(Map("value" -> ""))
          val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", ""))
          val result = route(application, request).value
          val view = application.injector.instanceOf[DetailsSelectItemView]

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, item1, CnCodeInformation("", "", `1`))(dataRequest(request), messages(application)).toString
        }
      }

    }
  }
}
