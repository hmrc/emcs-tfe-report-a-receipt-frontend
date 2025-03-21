import sbt.*

object AppDependencies {

  val playSuffix = "-play-30"
  val scalatestVersion = "3.2.19"
  val hmrcBootstrapVersion = "9.11.0"
  val hmrcMongoVersion = "2.6.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix" % "11.2.0",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix" %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"         %  hmrcMongoVersion,
    "com.google.inject"       %   "guice"                         % "5.1.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix" % hmrcBootstrapVersion,
    "org.scalatestplus"       %%  "scalacheck-1-18"           % s"$scalatestVersion.0",
    "org.scalatestplus.play"  %%  "scalatestplus-play"        % "7.0.1",
    "org.scalamock"           %%  "scalamock"                 % "5.2.0",
    "org.jsoup"               %   "jsoup"                     % "1.18.1",
    "com.vladsch.flexmark"    %   "flexmark-all"              % "0.64.8"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
