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
import mocks.services.{MockGetCnCodeInformationService, MockGetPackagingTypesService, MockGetWineOperationsService, MockUserAnswersService}
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, GetPackagingTypesService, GetWineOperationsService}
import utils.JsonOptionFormatter
import views.html.ItemDetailsView

import scala.concurrent.Future

class ItemDetailsControllerSpec extends SpecBase
  with JsonOptionFormatter
  with MockUserAnswersService
  with MockGetCnCodeInformationService
  with MockGetPackagingTypesService
  with MockGetWineOperationsService {

  def onPageLoadUrl(idx: Int): String = routes.ItemDetailsController.onPageLoad(testErn, testArc, idx).url

  "ItemDetails Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
            bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
            bind[GetWineOperationsService].toInstance(mockGetWineOperationsService)
          )
          .build()

        MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(item1)))

        MockGetWineOperationsService.getWineOperations(Seq(item1)).returns(Future.successful(Seq(item1)))

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(Seq((item1, CnCodeInformation("", "", `1`)))))

        running(application) {
          val request = FakeRequest(GET, onPageLoadUrl(idx = 1))

          val result = route(application, request).value

          val view = application.injector.instanceOf[ItemDetailsView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(item1, CnCodeInformation("", "", `1`))(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to /select-items-give-information" - {

        "when the item doesn't exist in UserAnswers" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
              bind[GetWineOperationsService].toInstance(mockGetWineOperationsService))
            .build()

          running(application) {
            val request = FakeRequest(GET, onPageLoadUrl(idx = 3))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }

        "when one of the service calls returns no data" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
              bind[GetPackagingTypesService].toInstance(mockGetPackagingTypesService),
              bind[GetWineOperationsService].toInstance(mockGetWineOperationsService))
            .build()

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq()))

          MockGetWineOperationsService.getWineOperations(Seq()).returns(Future.successful(Seq()))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq()).returns(Future.successful(Seq()))

          running(application) {
            val request = FakeRequest(GET, onPageLoadUrl(idx = 1))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
          }
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService))
          .build()

        running(application) {
          val request = FakeRequest(GET, onPageLoadUrl(3))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
