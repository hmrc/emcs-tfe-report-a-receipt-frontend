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
import forms.MoreInformationFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockGetPackagingTypesService, MockUserAnswersService}
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, GetPackagingTypesService, UserAnswersService}
import views.html.ItemMoreInformationView

import scala.concurrent.Future

class ItemMoreInformationControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockUserAnswersService
  with MockGetPackagingTypesService {

  class Fixture(answers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers = answers)
      .overrides(
        bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
        bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()

    val itemWithPackaging = item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
    val cnCodeInfo = CnCodeInformation("", "", `1`)

    lazy val view = application.injector.instanceOf[ItemMoreInformationView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new MoreInformationFormProvider()

  private lazy val itemDamageInformationRoute = routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, NormalMode).url
  private lazy val itemDamageInformationSubmitAction = routes.ItemMoreInformationController.submitItemDamageInformation(testErn, testArc, 1, NormalMode)

  "MoreInformation Controller" - {

    Seq(
      (AddItemDamageInformationPage(1), ItemDamageInformationPage(1), itemDamageInformationRoute, itemDamageInformationSubmitAction)
    ) foreach { case (yesNoPage, page, url, submitAction) =>

      s"for the '$page' page" - {

        val form = formProvider(page)

        "must return OK and the correct view for a GET" in new Fixture() {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackaging)).returns(Future.successful(Seq(
            (itemWithPackaging, cnCodeInfo)
          )))

          val request = FakeRequest(GET, url)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, page, submitAction, itemWithPackaging, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }

        "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers.set(page, Some("answer")))) {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackaging)).returns(Future.successful(Seq(
            (itemWithPackaging, cnCodeInfo)
          )))

          val request = FakeRequest(GET, url)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form.fill(Some("answer")), page, submitAction, itemWithPackaging, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }

        "must redirect to the next page when valid data is submitted" in new Fixture() {

          val updatedAnswers = emptyUserAnswers
            .set(yesNoPage, true)
            .set(page, Some("answer"))

          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("more-information", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }

        "must redirect to the next page when NO data is submitted" in new Fixture() {

          val updatedAnswers = emptyUserAnswers
            .set(yesNoPage, false)
            .set(page, None)

          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))
            .returns(Future.successful(updatedAnswers))

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("more-information", ""))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }

        "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackaging)).returns(Future.successful(Seq(
            (itemWithPackaging, cnCodeInfo)
          )))

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("more-information", "<>"))

          val boundForm = form.bind(Map("more-information" -> "<>"))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, page, submitAction, itemWithPackaging, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }

        "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

          val request = FakeRequest(GET, url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }

        "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("more-information", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
