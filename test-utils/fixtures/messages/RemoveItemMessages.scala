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

object RemoveItemMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: Int => String
    val heading: Int => String
    val requiredError: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading: Int => String = i => s"Are you sure you want to remove item $i?"
    override val title: Int => String = i => title(heading(i))
    override val requiredError: String = "Select yes if you want to remove this item"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading: Int => String = i => s"Are you sure you want to remove item $i?"
    override val title: Int => String = i => title(heading(i))
    override val requiredError: String = "Select yes if you want to remove this item"
  }
}
