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

object AddItemSealsInformationMessages {

  object English extends AddItemInformationMessagesBase with BaseEnglish {
    override val heading = "More information about broken seal(s)"
    override val title = title(heading)
    override val legend = (i: Int) => s"Do you want to give information about item $i having broken seal(s)?"
    override val itemDetails = (i: Int) => s"View item $i details"
    val hiddenLegendText = (i: Int) => s"Do you want to give information about item $i having broken seal(s)"
  }

}
