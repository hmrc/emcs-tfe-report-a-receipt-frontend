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
import forms.ItemShortageOrExcessFormProvider
import mocks.services.MockUserAnswersService
import models.AcceptMovement.PartiallyRefused
import models.UnitOfMeasure.Kilograms
import models.WrongWithMovement.{Excess, Shortage}
import models.{ItemShortageOrExcessModel, NormalMode}
import navigation.{FakeNavigator, Navigator}
import pages.AcceptMovementPage
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, RefusedAmountPage, RefusingAnyAmountOfItemPage, SelectItemsPage}
import play.api.data.FormError
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.ItemShortageOrExcessView

import scala.concurrent.Future

class ItemShortageOrExcessControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ItemShortageOrExcessFormProvider()
  val form = formProvider()

  lazy val itemShortageOrExcess = ItemShortageOrExcessModel(
    wrongWithItem = Excess,
    amount = 12.123,
    additionalInfo = Some("info")
  )

  lazy val defaultUserAnswers = emptyUserAnswers.set(SelectItemsPage(1), item1.itemUniqueReference)

  lazy val itemShortageOrExcessRoute = routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, idx = 1, NormalMode).url

  "ItemShortageOrExcess Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(defaultUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, itemShortageOrExcessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemShortageOrExcessView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          action = routes.ItemShortageOrExcessController.onSubmit(testErn, testArc, 1, NormalMode),
          unitOfMeasure = Kilograms
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = defaultUserAnswers.set(ItemShortageOrExcessPage(1), itemShortageOrExcess)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, itemShortageOrExcessRoute)

        val view = application.injector.instanceOf[ItemShortageOrExcessView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(itemShortageOrExcess),
          action = routes.ItemShortageOrExcessController.onSubmit(testErn, testArc, 1, NormalMode),
          unitOfMeasure = Kilograms
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted (shortage equal to Quantity)" in {

      MockUserAnswersService.set().returns(Future.successful(defaultUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(defaultUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, itemShortageOrExcessRoute)
            .withFormUrlEncodedBody(
              ("shortageOrExcess", Shortage.toString),
              ("amount", item1.quantity.toString()),
              ("additionalInformation", "info")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the next page when valid data is submitted (shortage less than Quantity)" - {
      "default" in {

        MockUserAnswersService.set().returns(Future.successful(defaultUserAnswers))

        val application =
          applicationBuilder(userAnswers = Some(defaultUserAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, itemShortageOrExcessRoute)
              .withFormUrlEncodedBody(
                ("shortageOrExcess", Shortage.toString),
                ("amount", (item1.quantity - 0.001).toString()),
                ("additionalInformation", "info")
              )

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "when Partially refused - no RefusedAmountPage value" in {

        MockUserAnswersService.set().returns(Future.successful(defaultUserAnswers))

        val application =
          applicationBuilder(userAnswers = Some(defaultUserAnswers
            .set(RefusingAnyAmountOfItemPage(1), true)
            .set(AcceptMovementPage, PartiallyRefused)
          ))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, itemShortageOrExcessRoute)
              .withFormUrlEncodedBody(
                ("shortageOrExcess", Shortage.toString),
                ("amount", (item1.quantity - 0.001).toString()),
                ("additionalInformation", "info")
              )

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "when Partially refused - with a RefusedAmountPage value" in {

        MockUserAnswersService.set().returns(Future.successful(defaultUserAnswers))

        val application =
          applicationBuilder(userAnswers = Some(defaultUserAnswers
            .set(RefusingAnyAmountOfItemPage(1), true)
            .set(AcceptMovementPage, PartiallyRefused)
            .set(RefusedAmountPage(1), BigDecimal(1))
          ))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, itemShortageOrExcessRoute)
              .withFormUrlEncodedBody(
                ("shortageOrExcess", Shortage.toString),
                ("amount", (item1.quantity - 1.001).toString()),
                ("additionalInformation", "info")
              )

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "must return a Bad Request and errors when valid data is submitted but shortage amount exceeds the quantity" in {

      val application = applicationBuilder(userAnswers = Some(defaultUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, itemShortageOrExcessRoute)
            .withFormUrlEncodedBody(
              ("shortageOrExcess", Shortage.toString),
              ("amount", (item1.quantity + 0.001).toString()),
              ("additionalInformation", "info")
            )

        val boundForm = form.bind(Map(
          ("shortageOrExcess", Shortage.toString),
          ("amount", (item1.quantity + 0.001).toString()),
          ("additionalInformation", "info")
        ))
          .withError(FormError("amount", "itemShortageOrExcess.amount.error.tooLarge", Seq(item1.quantity)))

        val view = application.injector.instanceOf[ItemShortageOrExcessView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          action = routes.ItemShortageOrExcessController.onSubmit(testErn, testArc, 1, NormalMode),
          unitOfMeasure = Kilograms
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must return a Bad Request and errors when valid data is submitted but shortage amount exceeds the quantity minus the amount already refused" in {

      val refusedAmount: BigDecimal = 10.125

      val userAnswers = defaultUserAnswers
        .set(AcceptMovementPage, PartiallyRefused)
        .set(RefusingAnyAmountOfItemPage(1), true)
        .set(RefusedAmountPage(1), refusedAmount)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, itemShortageOrExcessRoute)
            .withFormUrlEncodedBody(
              ("shortageOrExcess", Shortage.toString),
              ("amount", ((item1.quantity - refusedAmount) + 0.001).toString()),
              ("additionalInformation", "info")
            )

        val boundForm = form.bind(Map(
          ("shortageOrExcess", Shortage.toString),
          ("amount", ((item1.quantity - refusedAmount) + 0.001).toString()),
          ("additionalInformation", "info")
        ))
          .withError(FormError("amount", "itemShortageOrExcess.amount.error.tooLarge", Seq(item1.quantity - refusedAmount)))

        val view = application.injector.instanceOf[ItemShortageOrExcessView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          action = routes.ItemShortageOrExcessController.onSubmit(testErn, testArc, 1, NormalMode),
          unitOfMeasure = Kilograms
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(defaultUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, itemShortageOrExcessRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ItemShortageOrExcessView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          action = routes.ItemShortageOrExcessController.onSubmit(testErn, testArc, 1, NormalMode),
          unitOfMeasure = Kilograms
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, itemShortageOrExcessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, itemShortageOrExcessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
