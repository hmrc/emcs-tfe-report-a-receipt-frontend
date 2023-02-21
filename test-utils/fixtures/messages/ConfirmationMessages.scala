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

object ConfirmationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val reference: String => String
    val whatNextH2: String
    val whatNextP1: String
    val whatNextP2: String
    val whatNextP3: String
    val returnToMovement: String
    val feedback: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = title("Report of receipt submitted")
    override val heading = "Report of receipt submitted"
    override val reference: String => String = "Your reference is " + _
    override val whatNextH2 = "What happens next"
    override val whatNextP1 = "Your movement will be updated to confirm that this report of receipt has been submitted successfully."
    override val whatNextP2 = "This may take up to 15 minutes."
    override val whatNextP3 = "Print this screen if you want to make a record of your completed submission."
    override val returnToMovement = "Return to view your movement"
    override val feedback = "What did you think of this service? (opens in new tab) (takes 30 seconds)"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title: String = title("Report of receipt submitted")
    override val heading = "Report of receipt submitted"
    override val reference: String => String = "Your reference is " + _
    override val whatNextH2 = "What happens next"
    override val whatNextP1 = "Your movement will be updated to confirm that this report of receipt has been submitted successfully."
    override val whatNextP2 = "This may take up to 15 minutes."
    override val whatNextP3 = "Print this screen if you want to make a record of your completed submission."
    override val returnToMovement = "Return to view your movement"
    override val feedback = "What did you think of this service? (yn agor tab newydd) (takes 30 seconds)"
  }
}
