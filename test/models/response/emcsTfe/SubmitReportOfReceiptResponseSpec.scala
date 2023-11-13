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
import fixtures.SubmitReportOfReceiptFixtures
import play.api.libs.json.{JsPath, JsSuccess, Json}


class SubmitReportOfReceiptResponseSpec extends SpecBase with SubmitReportOfReceiptFixtures {

  "SubmitReportOfReceiptResponse" - {
    "should read from json - ChRIS" in {
      Json.fromJson[SubmitReportOfReceiptResponse](successResponseChRISJson) mustBe JsSuccess(successResponseChRIS, JsPath \ "receipt")
    }
    "should read from json - EIS" in {
      Json.fromJson[SubmitReportOfReceiptResponse](successResponseEISJson) mustBe JsSuccess(successResponseEIS, JsPath \ "message")
    }
    "should write to json - ChRIS" in {
      Json.toJson(successResponseChRIS) mustBe successResponseJson
    }
    "should write to json - EIS" in {
      Json.toJson(successResponseEIS) mustBe successResponseJson
    }
  }
}