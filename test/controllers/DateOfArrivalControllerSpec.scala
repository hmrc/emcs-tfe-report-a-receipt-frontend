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
import forms.DateOfArrivalFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.DateOfArrivalPage
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import utils.TimeMachine
import views.html.DateOfArrivalView

import java.time.{Instant, LocalDateTime}
import scala.concurrent.Future

class DateOfArrivalControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
    lazy val view = application.injector.instanceOf[DateOfArrivalView]
    implicit lazy val msgs: Messages = messages(application)
  }

  val fixedNow = LocalDateTime.now()
  val dateOfDispatch = fixedNow.minusDays(3)

  val timeMachine = new TimeMachine {
    override def now(): LocalDateTime = fixedNow
    override def instant(): Instant = Instant.now()
  }

  val formProvider = new DateOfArrivalFormProvider(timeMachine)
  private def form(implicit messages: Messages) = formProvider(dateOfDispatch.toLocalDate)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = fixedNow.toLocalDate

  lazy val dateOfArrivalRoute = routes.DateOfArrivalController.onPageLoad(testErn, testArc, NormalMode).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, dateOfArrivalRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, dateOfArrivalRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "DateOfArrival Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val result = route(application, getRequest()).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(getRequest()), msgs).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.set(DateOfArrivalPage, validAnswer)
    )) {
      running(application) {

        val result = route(application, getRequest()).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode)(dataRequest(getRequest()), msgs).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        val updatedAnswers = emptyUserAnswers.set(DateOfArrivalPage, validAnswer)
        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val result = route(application, postRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, dateOfArrivalRoute).withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(postRequest()), msgs).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val result = route(application, getRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val result = route(application, postRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
