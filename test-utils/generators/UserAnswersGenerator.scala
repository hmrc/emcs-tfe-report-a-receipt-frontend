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

package generators

import fixtures.BaseFixtures
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage, ItemShortageOrExcessPage, RefusedAmountPage, RefusingAnyAmountOfItemPage}
import pages.unsatisfactory.{HowGiveInformationPage, WrongWithMovementPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues with BaseFixtures {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(RefusedAmountPage, JsValue)] ::
    arbitrary[(RefusingAnyAmountOfItemPage, JsValue)] ::
    arbitrary[(ItemDamageInformationPage, JsValue)] ::
    arbitrary[(ItemShortageOrExcessPage, JsValue)] ::
    arbitrary[(AddItemDamageInformationPage, JsValue)] ::
    arbitrary[(HowGiveInformationPage.type, JsValue)] ::
    arbitrary[(WrongWithMovementPage.type, JsValue)] ::
    arbitrary[(MoreInformationPage.type, JsValue)] ::
    arbitrary[(AddMoreInformationPage.type, JsValue)] ::
    arbitrary[(AcceptMovementPage.type, JsValue)] ::
    arbitrary[(DateOfArrivalPage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        data <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers (
        testInternalId,
        testErn,
        testArc,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
