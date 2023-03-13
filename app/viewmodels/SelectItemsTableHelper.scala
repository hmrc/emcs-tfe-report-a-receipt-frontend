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

import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import views.html.components.{link, list}

import javax.inject.Inject

class SelectItemsTableHelper @Inject()(link: link, list: list) {

  private[viewmodels] def headerRow(implicit messages: Messages): Option[Seq[HeadCell]] = Some(Seq(
    HeadCell(Text(messages("selectItems.table.heading.description"))),
    HeadCell(Text(messages("selectItems.table.heading.quantity"))),
    HeadCell(Text(messages("selectItems.table.heading.alcohol"))),
    HeadCell(Text(messages("selectItems.table.heading.packaging")))
  ))

  private[viewmodels] def dataRows(ern: String, arc: String, items: Seq[MovementItem])(implicit messages: Messages): Seq[Seq[TableRow]] = items.map { item =>
    Seq(
      TableRow(
        content = HtmlContent(link(
          link = controllers.routes.SelectItemsController.addItemToList(ern, arc, item.itemUniqueReference).url,
          messageKey = item.cnCode
        ))
      ),
      TableRow(
        content = Text(item.quantity.toString())
      ),
      TableRow(
        content = Text(item.alcoholicStrength match {
          case Some(strength) => messages("selectItems.table.row.alcohol", strength)
          case None => messages("selectItems.table.row.alcohol.na")
        })
      ),
      TableRow(
        content = HtmlContent(list(item.packaging.map(pckg =>
          Html(pckg.quantity.toString() + " x " + pckg.typeOfPackage)
        )))
      )
    )
  }

  def constructTable(movements: Seq[MovementItem])(implicit messages: Messages, request: DataRequest[_]): Table =
    Table(
      firstCellIsHeader = true,
      rows = dataRows(request.ern, request.arc, movements),
      head = headerRow
    )
}
