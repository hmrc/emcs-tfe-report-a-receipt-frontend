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

import config.AppConfig
import models.UnitOfMeasure.Kilograms
import models.response.emcsTfe.{MovementItem, Packaging}
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import views.html.components.{link, list}

import javax.inject.Inject

class ItemDetailsCardHelper @Inject()(link: link, list: list, appConfig: AppConfig) {
  //noinspection ScalaStyle
  def constructItemDetailsCard(item: MovementItem, cnCodeInformation: CnCodeInformation)(implicit messages: Messages): Seq[(HtmlContent, HtmlContent)] = {
    val commodityCodeRow: Option[(HtmlContent, HtmlContent)] = Some((
      HtmlContent(messages("itemDetails.key.commodityCode")),
      HtmlContent(link(link = appConfig.getUrlForCommodityCode(item.cnCode), messageKey = item.cnCode, opensInNewTab = true))
    ))

    val descriptionRow: Option[(HtmlContent, HtmlContent)] = Some((
      HtmlContent(messages("itemDetails.key.description")),
      HtmlContent(cnCodeInformation.cnCodeDescription)
    ))

    val quantityRow: Option[(HtmlContent, HtmlContent)] = Some((
      HtmlContent(messages("itemDetails.key.quantity")),
      HtmlContent(messages(
        "itemDetails.value.quantity",
        item.quantity.toString(),
        messages(s"unitOfMeasure.${cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure}.long")
      ))
    ))

    val grossWeightRow: Option[(HtmlContent, HtmlContent)] = Some((
      HtmlContent(messages("itemDetails.key.grossWeight")),
      HtmlContent(messages(
        "itemDetails.value.grossWeight",
        item.grossMass.toString(),
        messages(s"unitOfMeasure.$Kilograms.short")
      ))
    ))

    val netWeightRow: Option[(HtmlContent, HtmlContent)] = Some((
      HtmlContent(messages("itemDetails.key.netWeight")),
      HtmlContent(messages(
        "itemDetails.value.netWeight",
        item.netMass.toString(),
        messages(s"unitOfMeasure.$Kilograms.short")
      ))
    ))

    val alcoholicStrengthRow: Option[(HtmlContent, HtmlContent)] = {
      item.alcoholicStrength.map {
        strength =>
          (
            HtmlContent(messages("itemDetails.key.alcoholicStrength")),
            HtmlContent(messages("itemDetails.value.alcoholicStrength", strength))
          )
      }
    }

    val degreePlatoRow: Option[(HtmlContent, HtmlContent)] = {
      item.degreePlato.map {
        deg =>
          (
            HtmlContent(messages("itemDetails.key.degreePlato")),
            HtmlContent(messages("itemDetails.value.degreePlato", deg))
          )
      }
    }

    val designationOfOriginRow: Option[(HtmlContent, HtmlContent)] = {
      item.designationOfOrigin.map {
        designation =>
          (
            HtmlContent(messages("itemDetails.key.designationOfOrigin")),
            HtmlContent(designation)
          )
      }
    }

    val sizeOfProducerRow: Option[(HtmlContent, HtmlContent)] = {
      item.sizeOfProducer.map {
        size =>
          (
            HtmlContent(messages("itemDetails.key.sizeOfProducer")),
            HtmlContent(messages("itemDetails.value.sizeOfProducer", size))
          )
      }
    }

    val commercialDescriptionRow: Option[(HtmlContent, HtmlContent)] = {
      item.commercialDescription.map {
        description =>
          (
            HtmlContent(messages("itemDetails.key.commercialDescription")),
            HtmlContent(description)
          )
      }
    }

    val brandNameOfProductRow: Option[(HtmlContent, HtmlContent)] = {
      item.brandNameOfProduct.map {
        brandNameOfProduct =>
          (
            HtmlContent(messages("itemDetails.key.brandNameOfProduct")),
            HtmlContent(brandNameOfProduct)
          )
      }
    }

    val wineProductCategoryRow: Option[(HtmlContent, HtmlContent)] = {
      item.wineProduct.map(
        wineProduct =>
          (
            HtmlContent(messages("itemDetails.key.wineProductCategory")),
            HtmlContent(messages(s"wineProductCategory.${wineProduct.wineProductCategory}"))
          )

      )
    }

    val wineGrowingZoneCodeRow: Option[(HtmlContent, HtmlContent)] = {
      item.wineProduct.flatMap(
        _.wineGrowingZoneCode.map(
          zoneCode =>
            (
              HtmlContent(messages("itemDetails.key.wineGrowingZoneCode")),
              HtmlContent(zoneCode)
            )
        )
      )
    }

    val thirdCountryOfOriginRow: Option[(HtmlContent, HtmlContent)] = {
      item.wineProduct.flatMap(
        _.thirdCountryOfOrigin.map(
          country =>
            (
              HtmlContent(messages("itemDetails.key.thirdCountryOfOrigin")),
              HtmlContent(country)
            )
        )
      )
    }

    val wineOperationsRow: Option[(HtmlContent, HtmlContent)] = {
      item.wineProduct match {
        case Some(wineProduct) => Some(
          wineProduct.wineOperations match {
            case Some(values) if values.nonEmpty =>
              (
                HtmlContent(messages("itemDetails.key.wineOperations")),
                HtmlContent(list(values.map(Html(_))))
              )
            case _ =>
              (
                HtmlContent(messages("itemDetails.key.wineOperations")),
                HtmlContent(messages("itemDetails.value.wineOperations.none"))
              )
          }
        )
        case None => None
      }
    }

    val wineOtherInformationRow: Option[(HtmlContent, HtmlContent)] = {
      item.wineProduct.flatMap(
        _.otherInformation.map(
          otherInformation =>
            (
              HtmlContent(messages("itemDetails.key.wineOtherInformation")),
              HtmlContent(otherInformation)
            )
        )
      )
    }

    val rows: Seq[Option[(HtmlContent, HtmlContent)]] = Seq(
      commodityCodeRow,
      descriptionRow,
      quantityRow,
      grossWeightRow,
      netWeightRow,
      alcoholicStrengthRow,
      degreePlatoRow,
      designationOfOriginRow,
      sizeOfProducerRow,
      commercialDescriptionRow,
      brandNameOfProductRow,
      wineProductCategoryRow,
      wineGrowingZoneCodeRow,
      thirdCountryOfOriginRow,
      wineOperationsRow,
      wineOtherInformationRow
    )

    rows.flatten
  }

  //noinspection ScalaStyle
  def constructPackagingTypeCard(packaging: Packaging)(implicit messages: Messages): Seq[(HtmlContent, HtmlContent)] = {

    val typeRow: Option[(HtmlContent, HtmlContent)] = {
      Some((
        HtmlContent(messages("itemDetails.packaging.key.type")),
        HtmlContent(packaging.typeOfPackage)
      ))
    }

    val quantityRow: Option[(HtmlContent, HtmlContent)] = {
      packaging.quantity.map {
        value => (
          HtmlContent(messages("itemDetails.packaging.key.quantity")),
          HtmlContent(value.toString())
        )
      }
    }

    val identityOfCommercialSealRow: Option[(HtmlContent, HtmlContent)] = {
      packaging.identityOfCommercialSeal.map {
        value =>
          (
            HtmlContent(messages("itemDetails.packaging.key.identityOfCommercialSeal")),
            HtmlContent(value)
          )
      }
    }

    val sealInformationRow: Option[(HtmlContent, HtmlContent)] = {
      packaging.sealInformation.map {
        value =>
          (
            HtmlContent(messages("itemDetails.packaging.key.sealInformation")),
            HtmlContent(value)
          )
      }
    }

    val shippingMarksRow: Option[(HtmlContent, HtmlContent)] = {
      packaging.shippingMarks.map {
        value =>
          (
            HtmlContent(messages("itemDetails.packaging.key.shippingMarks")),
            HtmlContent(value)
          )
      }
    }

    val rows: Seq[Option[(HtmlContent, HtmlContent)]] = Seq(
      typeRow,
      quantityRow,
      identityOfCommercialSealRow,
      sealInformationRow,
      shippingMarksRow,
    )

    rows.flatten
  }
}
