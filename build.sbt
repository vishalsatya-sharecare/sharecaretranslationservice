
import sbt.Resolver
import Dependencies._
import Resolvers._

// TODO - Change Project Name
val projectName = "audience-translation-service"

//@formatter:off
val settings = Seq(
  organization := "com.sharecare"
  , name := projectName
  , version := "1.0.0"
  , description := "DHS Audience Proxy"
  , scalaVersion := "2.11.11"
  , credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
  , cleanFiles <+= target { dir => dir / "universal" }
  , resolvers ++= Resolver.jcenterRepo +: Resolver.bintrayRepo("scalaz", "releases") +: {
    sharecareResolvers ++ Seq(
      Resolver.defaultLocal,
      "Artima Maven Repository" at "http://repo.artima.com/releases"
    )
  }
  , PlayKeys.devSettings += ("config.file", "./conf/lockerbox.properties")
  , coverageExcludedPackages := "<empty>;.*\\.html;Reverse.*;router\\.*"
  , libraryDependencies ++= ws +: (requiredDependencies ++ exampleDependencies)
  , parallelExecution in ThisBuild := false
  , coverageEnabled in(Test, test) := true
  , coverageFailOnMinimum in(Test, test) := true
//  , coverageMinimum := 95
  , scalacOptions += "-Ypartial-unification"
  , showSuccess := false
  , testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oN")
  , javaOptions in Universal ++= Seq(
    "-java-home /usr/lib/jvm/java-1.8.0-amazon-corretto"
  )
)
//@formatter:on

val universe = {
  // prevents builds from tacking on version to the package
  Seq(packageName := name.value)
}

// ensure lockerbox.properties and eventserver configuration files are not packaged for deployment
lazy val zipping = {
  val lockerbox = "conf/lockerbox.properties"
  val eventmodule = "conf/local-eventserver.conf"

  mappings --= Seq(baseDirectory.value / lockerbox -> lockerbox, baseDirectory.value / eventmodule -> eventmodule)
}


lazy val root = Project(id = projectName, base = file("."))
  .configs(IntegrationTest)
  .configs(Test)
  .enablePlugins(PlayScala, NewRelic, BuildInfoPlugin, CopyPasteDetector)
  .settings(settings)
  .settings(inConfig(Test)(testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oI", "-W", "120", "60")) ++ Seq(
    scalaSource in Test := baseDirectory.value / "test"
  ))
  .settings(inConfig(IntegrationTest)(Defaults.testSettings) ++ Seq(
    scalaSource in IntegrationTest := baseDirectory.value / "it",
    parallelExecution in IntegrationTest := false
  ))
  .settings(inConfig(Compile)(inTask(doc)(sources := Seq.empty) ++ inTask(packageDoc)(publishArtifact := false)))
  .settings(inConfig(Universal)(universe))
  .settings(inConfig(Universal)(inTask(packageZipTarball)(zipping)))

