name         := """Persona-API"""
organization := "Project Persona"
version      := "0.1-SNAPSHOT"
scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Websudos releases" at "https://dl.bintray.com/websudos/oss-releases/"
)

libraryDependencies ++= {
  val akkaHttpV = "2.0.3"
  val scalaTestV  = "3.0.0-M15"
  val scalaMockV = "3.2.2"
  val nimbusOAuth = "5.1"
  val nscalaTimeV = "2.6.0"
  val phantomV = "1.22.0"
  val scalazV = "7.1.3"
  val slickV = "3.1.1"
  val postgresV = "9.4.1207"
  val jodaConvertV = "1.8"
  val jbcryptV = "0.3m"
  val slickJodaMapperV = "2.1.0"


  Seq(
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaHttpV,
    "com.typesafe.slick" %% "slick" % slickV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "org.postgresql" % "postgresql" % postgresV,
    "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperV,
    "org.joda" % "joda-convert" % jodaConvertV,
    "org.mindrot" % "jbcrypt" % jbcryptV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % scalaMockV % "test",
    "com.nimbusds" % "oauth2-oidc-sdk" % nimbusOAuth,
    "com.github.nscala-time" %% "nscala-time" % nscalaTimeV,
    "org.scalaz" %% "scalaz-core" % scalazV,
    "com.websudos" %% "phantom-dsl" % phantomV,
    "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperV

  )
}

// Coveralls settings
ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 70
ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false
ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := true

