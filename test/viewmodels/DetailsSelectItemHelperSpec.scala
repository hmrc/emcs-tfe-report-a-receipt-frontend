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
import fixtures.messages.DetailsSelectItemMessages
import models.ReferenceDataUnitOfMeasure
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.TableRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}

class DetailsSelectItemHelperSpec extends SpecBase {

  lazy val app = applicationBuilder().build()
  implicit lazy val msgs = messages(app)


  "DetailsSelectItemHelperHelper" - {

    Seq(DetailsSelectItemMessages.English, DetailsSelectItemMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val messages: Messages = messagesApi(app).preferred(Seq(langMessages.lang))

        lazy val helper = new DetailsSelectItemHelper

        "should create the TableRows" - {

          val cnCodeInformation = CnCodeInformation("cn code description", "excise product code description", ReferenceDataUnitOfMeasure.`2`)

          def expectedSummaryTable(
                                    item:MovementItem,
                                    includeBrandName: Boolean = true,
                                    includeCommercialDescription: Boolean = true,
                                    includeAlcoholicStrength: Boolean = true,
                                    includeDensity: Boolean = true): Seq[Seq[TableRow]] = {

            def createTableRow(key: String, value: String): Option[Seq[TableRow]] = {
              Some(
                Seq(
                  TableRow(content = Text(key)),
                  TableRow(content = HtmlContent(value))
                )
              )
            }

            Seq(
              createTableRow(langMessages.tableProductCategoryKey, cnCodeInformation.exciseProductCodeDescription),

              createTableRow(langMessages.tableCNCodeKey, item.cnCode),

              if (includeBrandName) {
                createTableRow(langMessages.tableBrandNameKey, item.brandNameOfProduct.get)
              } else {
                None
              },

              if (includeCommercialDescription) {
                createTableRow(langMessages.tableCommercialDescriptionKey, item.commercialDescription.get)
              } else {
                None
              },
              createTableRow(langMessages.tableQuantityKey, langMessages.quantityValue(item.quantity)),

              if (includeAlcoholicStrength) {
                createTableRow(langMessages.tableAlcoholStrengthKey, langMessages.alcoholicStrengthValue(item.alcoholicStrength.get))
              } else {
                None
              },

              if (includeDensity) {
                createTableRow(langMessages.tableDensityKey, langMessages.densityValue(item.density.get))
              } else {
                None
              },

              createTableRow(langMessages.tablePackaging, s"${item.packaging.head.quantity.get} ${item.packaging.head.typeOfPackage}")
            ).flatten
          }

          "when brandName is not empty" in {
            val testItem = item1.copy(brandNameOfProduct = Some("brand name"))
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem)
          }

          "when brandName is empty" in {
            val testItem = item1.copy(brandNameOfProduct = None)
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem, includeBrandName = false)
          }

          "when commercialDescription is not empty" in {
            val testItem = item1.copy(commercialDescription = Some("commercial description"))
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem)
          }

          "when commercialDescription is empty" in {
            val testItem = item1.copy(commercialDescription = None)
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem, includeCommercialDescription = false)
          }

          "when alcoholicStrength is not empty" in {
            val testItem = item1.copy(alcoholicStrength = Some(BigDecimal(1)))
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem)
          }

          "when alcoholicStrength is empty" in {
            val testItem = item1.copy(alcoholicStrength = None)
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem, includeAlcoholicStrength = false)
          }

          "when density is not empty" in {
            val testItem = item1.copy(density = Some(BigDecimal(1)))
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem)
          }

          "when density is empty" in {
            val testItem = item1.copy(density = None)
            helper.constructItemSummaryRows(testItem, cnCodeInformation)(messages) mustBe expectedSummaryTable(testItem, includeDensity = false)
          }
        }

      }
    }

  }
}
