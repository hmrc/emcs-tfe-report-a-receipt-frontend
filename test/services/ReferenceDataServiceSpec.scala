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

package services

import base.SpecBase
import mocks.services.{MockGetCnCodeInformationService, MockGetPackagingTypesService, MockGetWineOperationsService}
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import models.response.{PackagingTypesException, ReferenceDataException, WineOperationsException}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataServiceSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockGetWineOperationsService
  with MockGetPackagingTypesService {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new ReferenceDataService(mockGetPackagingTypesService, mockGetWineOperationsService, mockGetCnCodeInformationService)

  val itemWithPackaging = item1.copy(packaging = Seq(boxPackage.copy(typeOfPackage = "Box")))
  val itemWithPackagingAndWine = itemWithPackaging.copy(wineProduct = Some(wineProduct.copy(wineOperations = Some(Seq("Acidification")))))
  val cnCodeInfo = CnCodeInformation("", "", `1`)

  ".getMovementItemsWithReferenceData(items: Seq[MovementItem])" - {

    "when packaging codes returns success" - {

      "when wine operations returns success" - {

        "when CnCodeInformation returns success" - {

          "must return successful items with all information updated" in {

            MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))
            MockGetWineOperationsService.getWineOperations(Seq(itemWithPackaging)).returns(Future.successful(Seq(itemWithPackagingAndWine)))
            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackagingAndWine))
              .returns(Future.successful(Seq(itemWithPackagingAndWine -> cnCodeInfo)))

            await(testService.getMovementItemsWithReferenceData(Seq(item1))) mustBe Seq(itemWithPackagingAndWine -> cnCodeInfo)
          }
        }

        "when CnCodeInformation returns failure" - {

          "must return successful items with all information updated" in {

            MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))
            MockGetWineOperationsService.getWineOperations(Seq(itemWithPackaging)).returns(Future.successful(Seq(itemWithPackagingAndWine)))
            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(itemWithPackagingAndWine))
              .returns(Future.failed(ReferenceDataException(s"Failed to match item with CN Code information")))

            intercept[ReferenceDataException](await(testService.getMovementItemsWithReferenceData(Seq(item1)))).message mustBe
              "Failed to match item with CN Code information"
          }
        }
      }

      "when wine operations returns failure" - {

        "must return successful items with all information updated" in {

          MockGetPackagingTypesService.getPackagingTypes(Seq(item1)).returns(Future.successful(Seq(itemWithPackaging)))
          MockGetWineOperationsService.getWineOperations(Seq(itemWithPackaging))
            .returns(Future.failed(WineOperationsException(s"Failed to retrieve wine operations from emcs-tfe-reference-data")))

          intercept[WineOperationsException](await(testService.getMovementItemsWithReferenceData(Seq(item1)))).message mustBe
            "Failed to retrieve wine operations from emcs-tfe-reference-data"
        }
      }
    }

    "when packaging codes returns failure" - {

      "must return successful items with all information updated" in {

        MockGetPackagingTypesService.getPackagingTypes(Seq(item1))
          .returns(Future.failed(PackagingTypesException(s"Failed to retrieve packaging types from emcs-tfe-reference-data")))

        intercept[PackagingTypesException](await(testService.getMovementItemsWithReferenceData(Seq(item1)))).message mustBe
          "Failed to retrieve packaging types from emcs-tfe-reference-data"
      }
    }
  }
}
