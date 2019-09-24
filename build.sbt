name := "untitled"

version := "0.1"

scalaVersion := "2.12.9"

val akkaHttpVersion = "10.1.9"
val slickVersion = "3.3.2"
val akkaVersion = "2.5.25"
val scalatestVersion = "3.0.8"
val postgresVersion = "42.2.6"
val unicornVersion = "1.3.3"
val kebsVersion = "1.6.3"
val slickPgVersion = "0.18.0"
val slf4jVersion = "1.7.28"
val flywayVersion = "6.0.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "org.postgresql" % "postgresql" % postgresVersion,
  "pl.iterators" %% "kebs-slick" % kebsVersion,
  "pl.iterators" %% "kebs-spray-json" % kebsVersion,
  "pl.iterators" %% "kebs-akka-http" % kebsVersion,
  "pl.iterators" %% "kebs-tagged" % kebsVersion,
  "com.github.tminglei" %% "slick-pg" % slickPgVersion,
  "com.github.tminglei" %% "slick-pg_spray-json" % slickPgVersion,
  "org.slf4j" % "slf4j-log4j12" % slf4jVersion,
  "org.flywaydb" % "flyway-core" % flywayVersion
)