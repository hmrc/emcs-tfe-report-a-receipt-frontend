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
import config.AppConfig
import fixtures.messages.ItemDetailsMessages
import models.ReferenceDataUnitOfMeasure
import models.response.emcsTfe.WineProduct
import models.response.referenceData.CnCodeInformation
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import views.html.components.{link, list}

class ItemDetailsCardHelperSpec extends SpecBase {

  "ItemDetailsCardHelper" - {

    Seq(ItemDetailsMessages.English, ItemDetailsMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val messages = messagesApi(app).preferred(Seq(langMessages.lang))

        lazy val link = app.injector.instanceOf[link]
        lazy val list = app.injector.instanceOf[list]
        lazy val appConfig = app.injector.instanceOf[AppConfig]

        lazy val helper = new ItemDetailsCardHelper(link, list, appConfig)

        "should render the ItemDetails card" - {

          val cnCodeInformation = CnCodeInformation("cn code description", ReferenceDataUnitOfMeasure.`1`)

          //noinspection ScalaStyle
          def card(wineProduct: Option[WineProduct]): Seq[(HtmlContent, HtmlContent)] = {
            Seq(
              Seq((
                HtmlContent(langMessages.commodityCodeKey),
                HtmlContent(link(link = appConfig.getUrlForCommodityCode(item1.cnCode), messageKey = item1.cnCode, opensInNewTab = true))
              )),
              Seq((
                HtmlContent(langMessages.descriptionKey),
                HtmlContent(cnCodeInformation.cnCodeDescription)
              )),
              Seq((
                HtmlContent(langMessages.quantityKey),
                HtmlContent(langMessages.quantityValue(item1.quantity))
              )),
              Seq((
                HtmlContent(langMessages.grossWeightKey),
                HtmlContent(langMessages.grossWeightValue(item1.grossMass))
              )),
              Seq((
                HtmlContent(langMessages.netWeightKey),
                HtmlContent(langMessages.netWeightValue(item1.netMass))
              )),
              Seq((
                HtmlContent(langMessages.alcoholicStrengthKey),
                HtmlContent(langMessages.alcoholicStrengthValue(item1.alcoholicStrength.get))
              )),
              Seq((
                HtmlContent(langMessages.degreePlatoKey),
                HtmlContent(langMessages.degreePlatoValue(item1.degreePlato.get))
              )),
              Seq((
                HtmlContent(langMessages.designationOfOriginKey),
                HtmlContent(item1.designationOfOrigin.get)
              )),
              Seq((
                HtmlContent(langMessages.sizeOfProducerKey),
                HtmlContent(langMessages.sizeOfProducerValue(item1.sizeOfProducer.get))
              )),
              Seq((
                HtmlContent(langMessages.commercialDescriptionKey),
                HtmlContent(item1.commercialDescription.get)
              )),
              Seq((
                HtmlContent(langMessages.brandNameOfProductKey),
                HtmlContent(item1.brandNameOfProduct.get)
              )),
              wineProduct match {
                case Some(_) => Seq((
                  HtmlContent(langMessages.wineProductCategoryKey),
                  HtmlContent(langMessages.wineWithoutPDOPGI)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq((
                  HtmlContent(langMessages.wineGrowingZoneCodeKey),
                  HtmlContent(value.wineGrowingZoneCode.get)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq((
                  HtmlContent(langMessages.thirdCountryOfOriginKey),
                  HtmlContent(value.thirdCountryOfOrigin.get)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq((
                  HtmlContent(langMessages.wineOperationsKey),
                  value.wineOperations match {
                    case Some(values) if values.nonEmpty => HtmlContent(list(values.map(v => Html(v))))
                    case _ => HtmlContent(langMessages.wineOperationsValueNone)
                  }
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq((
                  HtmlContent(langMessages.wineOtherInformationKey),
                  HtmlContent(value.otherInformation.get)
                ))
                case None => Seq()
              }
            ).flatten
          }

          "when wineOperations is not empty" in {
            helper.constructItemDetailsCard(item1, cnCodeInformation) mustBe card(Some(item1.wineProduct.get))
          }

          "when wineOperations is empty" in {
            val item1WithEmptyWineOperations = item1.copy(wineProduct = Some(wineProduct.copy(wineOperations = Some(Seq()))))
            helper.constructItemDetailsCard(item1WithEmptyWineOperations, cnCodeInformation) mustBe card(item1WithEmptyWineOperations.wineProduct)
          }

          "when wineProduct is empty" in {
            val item1WithEmptyWineOperations = item1.copy(wineProduct = None)
            helper.constructItemDetailsCard(item1WithEmptyWineOperations, cnCodeInformation) mustBe card(item1WithEmptyWineOperations.wineProduct)
          }
        }

        "should render the PackagingType card" in {

          helper.constructPackagingTypeCard(boxPackage) mustBe Seq(
            (
              HtmlContent(langMessages.packagingTypeKey),
              HtmlContent(boxPackage.typeOfPackage)
            ),
            (
              HtmlContent(langMessages.packagingQuantityKey),
              HtmlContent(boxPackage.quantity.get.toString())
            ),
            (
              HtmlContent(langMessages.packagingIdentityOfCommercialSealKey),
              HtmlContent(boxPackage.identityOfCommercialSeal.get)
            ),
            (
              HtmlContent(langMessages.packagingSealInformationKey),
              HtmlContent(boxPackage.sealInformation.get)
            ),
            (
              HtmlContent(langMessages.packagingShippingMarksKey),
              HtmlContent(boxPackage.shippingMarks.get)
            )
          )
        }
      }
    }
  }
}
