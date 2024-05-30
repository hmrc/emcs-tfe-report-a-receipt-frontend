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

object TimeoutMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val p1: String
    val signBackIn: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = title("For your security we signed you out")
    override val heading: String = "For your security we signed you out"
    override val p1: String = "We have saved your answers."
    override val signBackIn: String = "Sign back in to EMCS"
  }

}
