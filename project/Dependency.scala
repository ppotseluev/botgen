import sbt._

object Dependency {
  val kindProjectorVersion = "0.11.0"
  val taggingVersion = "2.2.1"
  val catsVersion = "2.2.0"
  val scalaGraphVersion = "1.13.2"
  val circeVersion = "0.13.0"
  val tapirVersion = "0.16.16"
  val scalaLoggingVersion = "3.9.2"
  val logbackVersion = "1.2.3"
  val sttpClientVersion = "2.2.9"
  val doobieVersion = "0.9.2"

  val kindProjector = "org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full

  val tagging = "com.softwaremill.common" %% "tagging" % taggingVersion
  val catsCore = "org.typelevel" %% "cats-core" % catsVersion
  val catsFree = "org.typelevel" %% "cats-free" % catsVersion
  val scalaGraph = "org.scala-graph" %% "graph-core" % scalaGraphVersion
  val circeCore = "io.circe" %% "circe-core" % circeVersion
  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeVersion
  val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion
  val tapirJsonCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion
  val tapirHttp4s = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val sttpClientCore = "com.softwaremill.sttp.client" %% "core" % sttpClientVersion
  val sttpClientCirce = "com.softwaremill.sttp.client" %% "circe" % sttpClientVersion
  val sttpClientHttp4sBackend = "com.softwaremill.sttp.client" %% "http4s-backend" % sttpClientVersion
  val doobieCore = "org.tpolecat" %% "doobie-core" % doobieVersion
}
