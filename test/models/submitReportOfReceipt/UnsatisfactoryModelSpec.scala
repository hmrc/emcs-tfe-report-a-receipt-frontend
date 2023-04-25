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

package models.submitReportOfReceipt

import base.SpecBase
import fixtures.UnsatisfactoryModelFixtures
import models.WrongWithMovement
import play.api.libs.json.Json
class UnsatisfactoryModelSpec extends SpecBase with UnsatisfactoryModelFixtures {

  "UnsatisfactoryModel" - {

    WrongWithMovement.values.foreach { reason =>

      s"for reason '$reason'" - {

        "for the maximum number of fields" - {

          "be possible to serialise and de-serialise to/from JSON" in {
            Json.toJson(maxUnsatisfactoryModel(reason)).as[UnsatisfactoryModel] mustBe maxUnsatisfactoryModel(reason)
          }
        }

        "for the minimum number of fields" - {

          "be possible to serialise and de-serialise to/from JSON" in {
            Json.toJson(minUnsatisfactoryModel(reason)).as[UnsatisfactoryModel] mustBe minUnsatisfactoryModel(reason)
          }
        }
      }
    }
  }
}
