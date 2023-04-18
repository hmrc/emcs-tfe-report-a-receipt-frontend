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
import models.AcceptMovement.{PartiallyRefused, Refused, Satisfactory}
import models.WrongWithMovement.{Excess, Shortage}
import models.{ItemShortageOrExcessModel, UserAnswers, WrongWithMovement}
import pages.AcceptMovementPage
import pages.unsatisfactory.WrongWithMovementPage
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, SelectItemsPage}
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import views.html.partials.confirmation.{ExcessContent, RefusedContent, ShortageContent}

class ConfirmationContentHelperSpec extends SpecBase {

  class Fixture(userAnswers: UserAnswers = emptyUserAnswers) {

    implicit lazy val app = applicationBuilder(Some(userAnswers)).build()
    implicit lazy val request = dataRequest(FakeRequest(), userAnswers)

    implicit lazy val msgs = app.injector.instanceOf[MessagesApi].preferred(request)
    lazy val confirmationHelper = app.injector.instanceOf[ConfirmationContentHelper]
    lazy val refusedContent = app.injector.instanceOf[RefusedContent]
    lazy val shortageContent = app.injector.instanceOf[ShortageContent]
    lazy val excessContent = app.injector.instanceOf[ExcessContent]
  }

  "ConfirmationContentHelper" - {

    s".renderRefusedContent()" - {

      "when the Report of Receipt is Refused" - {

        lazy val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Refused)

        "must render the expected content" in new Fixture(userAnswers) {
          confirmationHelper.renderRefusedContent() mustBe Some(refusedContent())
        }
      }

      "when the Report of Receipt is PartiallyRefused" - {

        lazy val userAnswers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)

        "must render the expected content" in new Fixture(userAnswers) {
          confirmationHelper.renderRefusedContent() mustBe Some(refusedContent())
        }
      }

      "when the Report of Receipt is any other status" - {

        lazy val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Satisfactory)

        "must return None" in new Fixture(userAnswers) {
          confirmationHelper.renderRefusedContent() mustBe None
        }
      }
    }

    s".renderShortageContent()" - {

      "when the movement has a shortage" - {

        lazy val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage))

        "must render the expected content" in new Fixture(userAnswers) {
          confirmationHelper.renderShortageContent() mustBe Some(shortageContent())
        }
      }

      "when at least one item in the movement has a shortage" - {

        lazy val userAnswers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Shortage, 1, None))

        "must render the expected content" in new Fixture(userAnswers) {
          confirmationHelper.renderShortageContent() mustBe Some(shortageContent())
        }
      }

      "when the whole movement is not a shortage" - {

        lazy val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Excess))

        "must return None" in new Fixture(userAnswers) {
          confirmationHelper.renderShortageContent() mustBe None
        }
      }

      "when none of the items has a shortage" - {

        lazy val userAnswers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Excess, 1, None))

        "must return None" in new Fixture(userAnswers) {
          confirmationHelper.renderShortageContent() mustBe None
        }
      }
    }

    s".renderExcessContent()" - {

      "when the movement has an excess" - {

        lazy val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Excess))

        "must render the expected content" in new Fixture(userAnswers) {
          confirmationHelper.renderExcessContent() mustBe Some(excessContent())
        }
      }

      "when at least one item in the movement has an excess" - {

        lazy val userAnswers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Excess, 1, None))

        "must render the expected content" in new Fixture(userAnswers) {
          confirmationHelper.renderExcessContent() mustBe Some(excessContent())
        }
      }

      "when the whole movement is not an excess" - {

        lazy val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage))

        "must return None" in new Fixture(userAnswers) {
          confirmationHelper.renderExcessContent() mustBe None
        }
      }

      "when none of the items has an excess" - {

        lazy val userAnswers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Shortage, 1, None))

        "must return None" in new Fixture(userAnswers) {
          confirmationHelper.renderExcessContent() mustBe None
        }
      }
    }
  }
}
