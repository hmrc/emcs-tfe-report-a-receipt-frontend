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

package fixtures.messages

object ContinueDraftMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val p1: String
    val inset: String
    val continueButton: String
    val startAgainLink: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "A report of receipt is already in progress for this movement"
    override val title = title(heading)
    override val p1: String = "You can continue with this report of receipt, or start again."
    override val inset: String = "If you choose to start again the details already entered to receipt this movement will not be saved."
    override val continueButton: String = "Continue"
    override val startAgainLink: String = "Start again"
  }

}
