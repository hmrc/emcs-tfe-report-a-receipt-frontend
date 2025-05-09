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

package config

import featureswitch.core.config.FeatureSwitching
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  implicit val config: AppConfig = this

  lazy val host: String = configuration.get[String]("host")
  lazy val appName: String = configuration.get[String]("appName")
  lazy val deskproName: String = configuration.get[String]("deskproName")

  lazy val loginUrl: String = configuration.get[String]("urls.login")

  def loginContinueUrl(ern: String, arc: String): String = configuration.get[String]("urls.loginContinue") + s"/$ern/$arc"

  lazy val signOutUrl: String = configuration.get[String]("urls.signOut")
  lazy val loginGuidance: String = configuration.get[String]("urls.loginGuidance")
  lazy val registerGuidance: String = configuration.get[String]("urls.registerGuidance")
  lazy val signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  lazy val tradeTariffCommoditiesUrl: String = configuration.get[String]("urls.tradeTariffCommodities")

  def getUrlForCommodityCode(code: String): String = s"$tradeTariffCommoditiesUrl/${code}00"

  lazy val contactHmrcUrl: String = configuration.get[String]("urls.contactHmrc")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")
  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName"

  def emcsTfeHomeUrl(ern: Option[String]): String =
    configuration.get[String]("urls.emcsTfeHome")

  def emcsMovementDetailsUrl(ern: String, arc: String): String =
    configuration.get[String]("urls.emcsTfeMovementDetails").replace(":ern", ern).replace(":arc", arc)

  def emcsMovementsUrl(ern: String): String =
    configuration.get[String]("urls.emcsTfeMovementsIn").replace(":ern", ern)

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  private def referenceDataService: String = servicesConfig.baseUrl("emcs-tfe-reference-data")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def referenceDataBaseUrl: String = s"$referenceDataService/emcs-tfe-reference-data"

  def emcsTfeFrontendBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-frontend")

  def destinationOfficeSuffix: String = configuration.get[String]("constants.destinationOfficeSuffix")

  private def nrsBrokerService: String = servicesConfig.baseUrl("nrs-broker")

  def nrsBrokerBaseUrl(): String = s"$nrsBrokerService/emcs-tfe-nrs-message-broker"

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  lazy val selfUrl: String = servicesConfig.baseUrl("emcs-tfe-report-a-receipt-frontend")

  lazy val emcsGeneralEnquiriesUrl: String = configuration.get[String]("urls.emcsGeneralEnquiries")

  def traderKnownFactsBaseUrl: String =
    emcsTfeService + "/emcs-tfe/trader-known-facts"

}
