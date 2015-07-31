name := """Persona-API"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val phantomVersion = "1.10.0"
val scalazVersion = "7.1.3"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "com.websudos" %% "phantom-dsl" % phantomVersion,
  "com.websudos" %% "phantom-testkit" % phantomVersion,
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.0" % "test",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "net.ceedubs" %% "ficus" % "1.1.2"
)

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo" at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging" at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "Twitter Repository" at "http://maven.twttr.com",
  "Websudos releases" at "https://dl.bintray.com/websudos/oss-releases/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/",
  Classpaths.sbtPluginReleases
)

// Coveralls settings
ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 70
ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false
ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := true
ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "<empty>;controllers\\..*Reverse.*;router\\..*Routes.*;"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
