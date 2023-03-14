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

object SelectItemsMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val tableHeadDescription: String
    val tableHeadQuantity: String
    val tableHeadAlcohol: String
    val tableHeadPackaging: String
    val alcoholRow: Option[BigDecimal] => String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Select an item to give information about"
    override val title = title(heading)
    override val tableHeadDescription = "Description"
    override val tableHeadQuantity = "Quantity"
    override val tableHeadAlcohol = "Alcohol"
    override val tableHeadPackaging = "Packaging"
    override val alcoholRow: Option[BigDecimal] => String = {
      case Some(strength) => strength + "%"
      case None => "N/A"
    }
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "Select an item to give information about"
    override val title = title(heading)
    override val tableHeadDescription = "Description"
    override val tableHeadQuantity = "Quantity"
    override val tableHeadAlcohol = "Alcohol"
    override val tableHeadPackaging = "Packaging"
    override val alcoholRow: Option[BigDecimal] => String = {
      case Some(strength) => strength + "%"
      case None => "N/A"
    }
  }
}
