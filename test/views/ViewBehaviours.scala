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

package views

import base.ViewSpecBase
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}

trait ViewBehaviours { _: ViewSpecBase =>

  def pageWithExpectedElementsAndMessages(checks: Seq[(String, String)])(implicit document: Document): Unit = checks foreach {
    case (selector, message) =>
      s"element with selector '$selector'" - {
        s"must have the message '$message'" in {
          Try {
            document.select(selector).first()
          } match {
            case Failure(_) => fail(s"Could not find element with CSS selector: '$selector'")
            case Success(element) => element.text() mustBe message
          }
        }
      }
  }
}

