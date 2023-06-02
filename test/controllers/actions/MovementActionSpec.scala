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

package controllers.actions

import base.SpecBase
import handlers.ErrorHandler
import mocks.connectors.MockGetMovementConnector
import models.requests.{MovementRequest, UserRequest}
import models.response.emcsTfe.GetMovementResponse
import models.response.{ErrorResponse, JsonValidationError}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.mockito.MockitoSugar
import play.api.Play
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovementActionSpec extends SpecBase with MockitoSugar with MockGetMovementConnector with BeforeAndAfterAll {

  lazy val app = applicationBuilder(userAnswers = None).build()
  implicit val hc = HeaderCarrier()
  implicit lazy val request = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)

  lazy val errorHandler = app.injector.instanceOf[ErrorHandler]

  lazy val movementAction = new MovementActionImpl(
    mockGetMovementConnector,
    errorHandler
  )

  override def beforeAll(): Unit = {
    Play.start(app)
  }

  override def afterAll(): Unit = {
    Play.stop(app)
  }

  class Harness(connectorResponse: Either[ErrorResponse, GetMovementResponse]) {

    MockGetMovementConnector.getMovement(testErn, testArc).returns(Future.successful(connectorResponse))

    val result = movementAction(testArc).invokeBlock(request, { movementRequest: MovementRequest[_] =>
      Future.successful(Ok)
    })
  }

  "MovementAction" - {

    "when the connector returns a Movement successfully for the requested ARC" - {

      "must execute the supplied block" in new Harness(Right(getMovementResponseModel)) {

        status(result) mustBe OK
      }
    }

    "when the connector does not return any data" - {

      "must render a BadRequest" in new Harness(Left(JsonValidationError)) {

        status(result) mustBe BAD_REQUEST
        Html(contentAsString(result)) mustBe errorHandler.badRequestTemplate
      }
    }
  }
}
