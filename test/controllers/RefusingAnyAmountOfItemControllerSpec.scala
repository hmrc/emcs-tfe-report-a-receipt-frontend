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
import forms.RefusingAnyAmountOfItemFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockGetPackagingTypesService, MockGetWineOperationsService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{RefusingAnyAmountOfItemPage, SelectItemsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, GetPackagingTypesService, GetWineOperationsService, UserAnswersService}
import views.html.RefusingAnyAmountOfItemView

import scala.concurrent.Future

class RefusingAnyAmountOfItemControllerSpec extends SpecBase with MockGetCnCodeInformationService
  with MockUserAnswersService
  with MockGetWineOperationsService
  with MockGetPackagingTypesService {

  class Fixture(answers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(answers)
        .overrides(
          bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
          bind[GetWineOperationsService].toInstance(mockGetWineOperationsService)
        )
        .build()

    lazy val view = application.injector.instanceOf[RefusingAnyAmountOfItemView]

    val cnCodeInfo = CnCodeInformation("", "", `1`)
  }

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new RefusingAnyAmountOfItemFormProvider()
  val form = formProvider()

  lazy val refusingAnyAmountOfItemRoute = routes.RefusingAnyAmountOfItemController.onPageLoad(testErn, testArc, 1, NormalMode).url
  lazy val submitAction = routes.RefusingAnyAmountOfItemController.onSubmit(testErn, testArc, 1, NormalMode)

  "RefusingAnyAmountOfItem Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(
      Some(emptyUserAnswers.set(SelectItemsPage(1), 1))
    ) {

      MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(item1)))
      MockGetWineOperationsService.getWineOperations(Seq(item1)).returns(Future.successful(Seq(item1)))
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq((item1, cnCodeInfo))))

      val request = FakeRequest(GET, refusingAnyAmountOfItemRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitAction, item1, cnCodeInfo)(dataRequest(request), messages(application)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers
        .set(SelectItemsPage(1), 1)
        .set(RefusingAnyAmountOfItemPage(1), true))
    ) {

      MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(item1)))
      MockGetWineOperationsService.getWineOperations(Seq(item1)).returns(Future.successful(Seq(item1)))
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq((item1, cnCodeInfo))))

      val request = FakeRequest(GET, refusingAnyAmountOfItemRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), submitAction, item1, cnCodeInfo)(dataRequest(request), messages(application)).toString
    }

    "must redirect to the next page when valid data is submitted" - {

      "and delete the rest of the answers when the input answer isn't the same as the current answer" in new Fixture(
        Some(emptyUserAnswers
          .set(SelectItemsPage(1), 1)
          .set(RefusingAnyAmountOfItemPage(1), false)
        )
      ) {

        val updatedAnswers = emptyUserAnswers
          .set(SelectItemsPage(1), 1)
          .set(RefusingAnyAmountOfItemPage(1), true)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val request = FakeRequest(POST, refusingAnyAmountOfItemRoute).withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }

      "and not delete the rest of the answers when the input answer is the same as the current answer" in new Fixture(
        Some(emptyUserAnswers
          .set(SelectItemsPage(1), 1)
          .set(RefusingAnyAmountOfItemPage(1), true)
        )
      ) {

        MockUserAnswersService.set().never()

        val request = FakeRequest(POST, refusingAnyAmountOfItemRoute).withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to /select-items" - {

      "when the item doesn't exist in UserAnswers" in new Fixture() {

        val request = FakeRequest(GET, refusingAnyAmountOfItemRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
      Some(emptyUserAnswers.set(SelectItemsPage(1), 1))
    ) {

      MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(item1)))
      MockGetWineOperationsService.getWineOperations(Seq(item1)).returns(Future.successful(Seq(item1)))
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq(
        (item1, cnCodeInfo)
      )))

      val request = FakeRequest(POST, refusingAnyAmountOfItemRoute).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitAction, item1, cnCodeInfo)(dataRequest(request), messages(application)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      val request = FakeRequest(GET, refusingAnyAmountOfItemRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      val request = FakeRequest(POST, refusingAnyAmountOfItemRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
    }
  }
}
