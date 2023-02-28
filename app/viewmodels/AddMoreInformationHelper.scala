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

import models.AcceptMovement.Satisfactory
import models.requests.DataRequest
import pages.{AcceptMovementPage, QuestionPage}

object AddMoreInformationHelper {

  private def infix()(implicit request: DataRequest[_]): String =
    if(request.userAnswers.get(AcceptMovementPage).contains(Satisfactory)) ".satisfactory" else ""

  def titleKey(page: QuestionPage[Boolean])(implicit request: DataRequest[_]): String =
    s"$page${infix()}.title"

  def headingKey(page: QuestionPage[Boolean])(implicit request: DataRequest[_]): String =
    s"$page${infix()}.heading"


}
