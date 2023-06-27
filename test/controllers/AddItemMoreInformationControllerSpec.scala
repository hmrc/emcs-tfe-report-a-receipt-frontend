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
import forms.AddMoreInformationFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockGetPackagingTypesService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, GetPackagingTypesService, UserAnswersService}
import views.html.AddItemMoreInformationView

import scala.concurrent.Future

class AddItemMoreInformationControllerSpec extends SpecBase
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
  }

  private val idx = 1

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AddMoreInformationFormProvider()

  lazy val addItemDamageInformationRoute = controllers.routes.AddItemMoreInformationController.loadItemDamageInformation(testErn, testArc, idx, NormalMode).url
  lazy val addItemDamageInformationSubmitAction = controllers.routes.AddItemMoreInformationController.submitItemDamageInformation(testErn, testArc, idx, NormalMode)

  Seq(
    (AddItemDamageInformationPage(idx), ItemDamageInformationPage(idx), addItemDamageInformationRoute, addItemDamageInformationSubmitAction)
  ) foreach { case (yesNoPage, infoPage, url, submitAction) =>

    s"for the '$yesNoPage' page" - {

      val form = formProvider(yesNoPage)

      "AddItemMoreInformation Controller" - {

        "must return OK and the correct view for a GET" in new Fixture() {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackaging)).returns(Future.successful(Seq(
            (itemWithPackaging, CnCodeInformation("", "", `1`))
          )))

          val request = FakeRequest(GET, url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AddItemMoreInformationView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            page = yesNoPage,
            action = submitAction,
            item = itemWithPackaging,
            cnCodeInformation = CnCodeInformation("", "", `1`)
          )(dataRequest(request), messages(application)).toString
        }

        "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers.set(yesNoPage, true))) {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackaging)).returns(Future.successful(Seq(
            (itemWithPackaging, CnCodeInformation("", "", `1`))
          )))

          val request = FakeRequest(GET, url)

          val view = application.injector.instanceOf[AddItemMoreInformationView]

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill(true),
            page = yesNoPage,
            action = submitAction,
            item = itemWithPackaging,
            cnCodeInformation = CnCodeInformation("", "", `1`)
          )(dataRequest(request), messages(application)).toString
        }

        "must redirect to the next page when valid data is submitted (true)" in new Fixture() {

          val updatedAnswers = emptyUserAnswers.set(yesNoPage, true)
          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }

        "must redirect to the next page when valid data is submitted (false) AND clear answers" in new Fixture() {

          val updatedAnswers = emptyUserAnswers
            .set(yesNoPage, false)
            .set(infoPage, None)

          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }

        "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackaging)).returns(Future.successful(Seq(
            (itemWithPackaging, CnCodeInformation("", "", `1`))
          )))

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val view = application.injector.instanceOf[AddItemMoreInformationView]

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            page = yesNoPage,
            action = submitAction,
            item = itemWithPackaging,
            cnCodeInformation = CnCodeInformation("", "", `1`)
          )(dataRequest(request), messages(application)).toString
        }

        "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

          val request = FakeRequest(GET, url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }

        "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

          val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
