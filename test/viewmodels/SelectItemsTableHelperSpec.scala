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

import base.SpecBase
import fixtures.messages.SelectItemsMessages
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, TableRow}
import views.html.components.{link, list}

class SelectItemsTableHelperSpec extends SpecBase {

  "SelectItemsTableHelper" - {

    Seq(SelectItemsMessages.English, SelectItemsMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))

        lazy val link = app.injector.instanceOf[link]
        lazy val list = app.injector.instanceOf[list]

        lazy val acceptMovementSummary = new SelectItemsTableHelper(link, list)

        "should render the correct header rows" in {

          acceptMovementSummary.headerRow mustBe Some(Seq(
            HeadCell(Text(langMessages.tableHeadDescription)),
            HeadCell(Text(langMessages.tableHeadQuantity)),
            HeadCell(Text(langMessages.tableHeadAlcohol)),
            HeadCell(Text(langMessages.tableHeadPackaging))
          ))
        }

        "should render the correct data rows" in {

          acceptMovementSummary.dataRows(
            ern = testErn,
            arc = testArc,
            items = Seq(item1, item2).zipWithIndex.map { case (l, i) => (l, CnCodeInformation(s"testdata${i + 1}", `1`)) }
          ) mustBe Seq(
            Seq(
              TableRow(
                content = HtmlContent(link(
                  link = controllers.routes.SelectItemsController.addItemToList(testErn, testArc, item1.itemUniqueReference).url,
                  messageKey = "testdata1"
                ))
              ),
              TableRow(
                content = Text(item1.quantity.toString() + " kg")
              ),
              TableRow(
                content = Text(langMessages.alcoholRow(item1.alcoholicStrength))
              ),
              TableRow(
                content = HtmlContent(list(Seq(
                  Html(boxPackage.quantity.toString() + " x " + boxPackage.typeOfPackage)
                )))
              )
            ),
            Seq(
              TableRow(
                content = HtmlContent(link(
                  link = controllers.routes.SelectItemsController.addItemToList(testErn, testArc, item2.itemUniqueReference).url,
                  messageKey = "testdata2"
                ))
              ),
              TableRow(
                content = Text(item2.quantity.toString() + " kg")
              ),
              TableRow(
                content = Text(langMessages.alcoholRow(item2.alcoholicStrength))
              ),
              TableRow(
                content = HtmlContent(list(Seq(
                  Html(boxPackage.quantity.toString() + " x " + boxPackage.typeOfPackage),
                  Html(cratePackage.quantity.toString() + " x " + cratePackage.typeOfPackage)
                )))
              )
            )
          )
        }
      }
    }
  }
}
