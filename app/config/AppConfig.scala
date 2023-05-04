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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) {

  lazy val host: String    = configuration.get[String]("host")
  lazy val appName: String = configuration.get[String]("appName")

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")

  def betaBannerFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$appName&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val signOutUrl: String       = configuration.get[String]("urls.signOut")

  lazy val contactHmrcUrl: String   = configuration.get[String]("urls.contactHmrc")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")
  lazy val feedbackFrontendSurveyUrl: String    = s"$feedbackFrontendHost/feedback/$appName"

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  lazy val emcsTfeHomeUrl: String = configuration.get[String]("urls.emcsTfeHome")
  def emcsMovementDetailsUrl(ern: String, arc: String): String =
    configuration.get[String]("urls.emcsTfeMovementDetails") + s"/$ern/$arc"

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  lazy val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")
  private def userAllowListService: String = servicesConfig.baseUrl("user-allow-list")
  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"
  def userAllowListBaseUrl: String = s"$userAllowListService/user-allow-list"

  def destinationOffice: String = configuration.get[String]("constants.destinationOffice")

  def allowListEnabled: Boolean = configuration.get[Boolean]("features.allowListEnabled")

  def internalAuthToken: String = configuration.get[String]("internal-auth.token")
}
