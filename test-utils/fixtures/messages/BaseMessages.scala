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

import play.api.i18n.Lang


sealed trait BaseMessages { _: i18n =>
  val opensInNewTab: String
  def arcSubheading(arc: String): String
  val lang: Lang
}

trait BaseEnglish extends BaseMessages with EN {
  override val opensInNewTab: String = "(opens in new tab)"
  override def arcSubheading(arc: String): String = s"Report of receipt for $arc"
}
object BaseEnglish extends BaseEnglish

trait BaseWelsh extends BaseMessages with CY {
  override val opensInNewTab: String = "(opens in new tab)"
  override def arcSubheading(arc: String): String = s"Report of receipt for $arc"
}
object BaseWelsh extends BaseWelsh
