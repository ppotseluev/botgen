import sbt._

object Dependency {
  val taggingVersion = "2.2.1"
  val catsVersion = "2.2.0"
  val scalaGraphVersion = "1.13.2"
  val circeVersion = "0.13.0"
  val tapirVersion = "0.16.16"

  val tagging = "com.softwaremill.common" %% "tagging" % taggingVersion
  val catsCore = "org.typelevel" %% "cats-core" % catsVersion
  val scalaGraph = "org.scala-graph" %% "graph-core" % scalaGraphVersion
  val circeCore = "io.circe" %% "circe-core" % circeVersion
  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion
  val tapirJsonCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion
  val tapirHttp4s = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion
}
