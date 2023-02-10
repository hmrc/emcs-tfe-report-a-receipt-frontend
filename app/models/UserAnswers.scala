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

package models

import play.api.libs.json._
import queries.{Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class UserAnswers(internalId: String,
                             ern: String,
                             arc: String,
                             data: JsObject = Json.obj(),
                             lastUpdated: Instant = Instant.now) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).asOpt.flatten

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): UserAnswers =
    cleanUp(page, Some(value)) {
      data.setObject(page.path, Json.toJson(value))
    }

  def remove[A](page: Settable[A]): UserAnswers =
    cleanUp(page) {
      data.removeObject(page.path)
    }

  private[models] def cleanUp[A](page: Settable[A], value: Option[A] = None): JsResult[JsObject] => UserAnswers = {
    case JsSuccess(updatedAnswers, _) =>
      page.cleanup(value, copy(data = updatedAnswers))
    case JsError(errors) =>
      throw JsResultException(errors)
  }
}

object UserAnswers {

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "internalId").read[String] and
      (__ \ "ern").read[String] and
      (__ \ "arc").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
      ) (UserAnswers.apply _)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "internalId").write[String] and
      (__ \ "ern").write[String] and
      (__ \ "arc").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
      ) (unlift(UserAnswers.unapply))
  }

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}