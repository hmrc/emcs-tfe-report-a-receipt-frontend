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

package models.response.referenceData

import models.ReferenceDataUnitOfMeasure
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class CnCodeInformation(cnCodeDescription: String, exciseProductCodeDescription: String, unitOfMeasureCode: ReferenceDataUnitOfMeasure)

object CnCodeInformation {
  implicit val reads: Reads[CnCodeInformation] = (
    (JsPath \ "cnCodeDescription").read[String] and
    (JsPath \ "exciseProductCodeDescription").read[String] and
      (JsPath \ "unitOfMeasureCode").read[Int].map(value => ReferenceDataUnitOfMeasure.enumerable.withName(value.toString).get)
  )(CnCodeInformation.apply _)

  implicit val writes: OWrites[CnCodeInformation] = Json.writes
}
