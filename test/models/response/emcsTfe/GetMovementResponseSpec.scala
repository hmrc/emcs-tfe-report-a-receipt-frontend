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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models.response.emcsTfe

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import play.api.libs.json.{JsSuccess, Json}


class GetMovementResponseSpec extends SpecBase with GetMovementResponseFixtures {

  "GetMovementResponse" - {
    "should read from json" in {
      Json.fromJson[GetMovementResponse](getMovementResponseJson) mustBe JsSuccess(getMovementResponseModel)
    }
    "should write to json" in {
      Json.toJson(getMovementResponseModel) mustBe getMovementResponseJson
    }
  }
}