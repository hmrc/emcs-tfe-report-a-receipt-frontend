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

package models.audit

import base.SpecBase
import fixtures.audit.SubmitReportOfReceiptAuditModelFixture._
import play.api.libs.json.Json
import models.audit.SubmitReportOfReceiptAuditModel

class SubmitReportOfReceiptAuditModelSpec extends SpecBase {

  "SubmitReportOfReceiptAuditModel" - {

    "should write a correct satisfactory audit json" in {
     submitRORAuditModelSatisfactoryModel.detail mustBe submitRORAuditModelSatisfactoryJson
    }

    "should write a correct unsatisfactory audit json" in {
      submitRORAuditModelUnSatisfactoryModel.detail mustBe submitRORAuditModelUnSatisfactoryJson
    }

    "should write a correct partially refused audit json" in {
      submitRORAuditModelPartiallyRefusedModel.detail mustBe submitRORAuditModelPartiallyRefusedJson
    }

    "should write a correct refused audit json" in {
      submitRORAuditModelRefusedModel.detail mustBe submitRORAuditModelRefusedJson
    }
  }

}
