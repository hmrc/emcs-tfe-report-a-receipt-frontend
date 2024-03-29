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
import fixtures.AddressModelFixtures
import play.api.libs.json.Json

class AddressModelSpec extends SpecBase with AddressModelFixtures {

  "AddressModel" - {

    "should for the maximum number of fields" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(maxAddressModel).as[AddressModel] mustBe maxAddressModel
      }
    }

    "should for the minimum number of fields" - {

      "must serialise and de-serialise to/from JSON" in {
        Json.toJson(minAddressModel).as[AddressModel] mustBe minAddressModel
      }
    }
  }
}
