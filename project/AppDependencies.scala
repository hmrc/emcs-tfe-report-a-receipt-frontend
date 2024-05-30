import sbt.*

object AppDependencies {

  val playSuffix = "-play-30"
  val scalatestVersion = "3.2.15"
  val hmrcBootstrapVersion = "8.6.0"
  val hmrcMongoVersion = "1.9.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %%  s"play-frontend-hmrc$playSuffix" % "8.5.0",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"  %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"          %  hmrcMongoVersion,
    "com.google.inject"       %   "guice"                          % "5.1.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix" % hmrcBootstrapVersion,
    "org.scalatestplus"       %%  "scalacheck-1-17"           % s"$scalatestVersion.0",
    "org.scalatestplus.play"  %%  "scalatestplus-play"        % "5.1.0",
    "org.scalamock"           %%  "scalamock"                 % "5.2.0",
    "org.jsoup"               %   "jsoup"                     % "1.15.4",
    "com.vladsch.flexmark"    %   "flexmark-all"              % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
