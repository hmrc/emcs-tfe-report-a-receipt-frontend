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

package viewmodels

import base.SpecBase
import models.AcceptMovement.Refused
import models.requests.DataRequest
import pages.{AcceptMovementPage, QuestionPage}
import play.api.libs.json.JsPath
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class OtherInformationHelperSpec extends SpecBase {

  val requestWithRefusedMovement: DataRequest[AnyContentAsEmpty.type] =
    dataRequest(
      FakeRequest(),
      emptyUserAnswers.set(AcceptMovementPage, Refused)
    )

  val request: DataRequest[AnyContentAsEmpty.type] =
    dataRequest(
      FakeRequest(),
      emptyUserAnswers
    )

  val testPage: QuestionPage[String] = new QuestionPage[String] {
    override def toString: String = "test"
    override def path: JsPath = JsPath \ toString
  }

  "OtherInformationHelper" - {
    "conditionalTitle" - {
      "should return the message for a refused movement" in {
        OtherInformationHelper.conditionalTitle(testPage)(requestWithRefusedMovement) mustBe "test.refused.title"
      }

      "should return the default message" in {
        OtherInformationHelper.conditionalTitle(testPage)(request) mustBe "test.title"
      }
    }

    "conditionalHeading" - {
      "should return the message for a refused movement" in {
        OtherInformationHelper.conditionalHeading(testPage)(requestWithRefusedMovement) mustBe "test.refused.heading"
      }

      "should return the default message" in {
        OtherInformationHelper.conditionalHeading(testPage)(request) mustBe "test.heading"
      }
    }
  }

}
