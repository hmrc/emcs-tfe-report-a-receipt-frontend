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
import fixtures.messages.AddMoreInformationMessages
import models.AcceptMovement.{Refused, Satisfactory}
import pages.{AcceptMovementPage, AddMoreInformationPage}
import play.api.test.FakeRequest

class AddMoreInformationHelperSpec extends SpecBase {

  "AddMoreInformationHelper" - {

    Seq(AddMoreInformationMessages.English, AddMoreInformationMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))

        "must return the correct title key" - {

          "when Answer for AcceptMovement is Satisfactory" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(AcceptMovementPage, Satisfactory))

            AddMoreInformationHelper.titleKey(AddMoreInformationPage) mustBe "addMoreInformation.satisfactory.title"
            langMessages.satisfactoryTitle must include(msgs(AddMoreInformationHelper.titleKey(AddMoreInformationPage)))
          }

          "when Answer for AcceptMovement is anything other than Satisfacatory" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(AcceptMovementPage, Refused))

            AddMoreInformationHelper.titleKey(AddMoreInformationPage) mustBe "addMoreInformation.title"
            langMessages.title must include(msgs(AddMoreInformationHelper.titleKey(AddMoreInformationPage)))
          }
        }

        "must return the correct heading key" - {

          "when Answer for AcceptMovement is Satisfactory" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(AcceptMovementPage, Satisfactory))

            AddMoreInformationHelper.headingKey(AddMoreInformationPage) mustBe "addMoreInformation.satisfactory.heading"
            msgs(AddMoreInformationHelper.headingKey(AddMoreInformationPage)) mustBe langMessages.satisfactoryHeading
          }

          "when Answer for AcceptMovement is anything other than Satisfacatory" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(AcceptMovementPage, Refused))

            AddMoreInformationHelper.headingKey(AddMoreInformationPage) mustBe "addMoreInformation.heading"
            msgs(AddMoreInformationHelper.headingKey(AddMoreInformationPage)) mustBe langMessages.heading
          }
        }
      }
    }
  }
}
