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

object AddedItemsMessages {

  sealed trait ViewMessages { _: i18n =>
    val titleSingular: String
    val titlePlural: Int => String
    val headingSingular: String
    val headingPlural: Int => String
    val requiredError: String
    val addAnother: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val headingSingular = "You have given information for 1 item"
    override val headingPlural = (n: Int) => s"You have given information for $n items"
    override val titleSingular = title(headingSingular)
    override val titlePlural = (n: Int) => title(headingPlural(n))
    override val requiredError: String = "Select yes if you want to give information about another item"
    override val addAnother: String = "Do you want to add another item?"
  }

}
