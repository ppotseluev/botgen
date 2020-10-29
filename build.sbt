import sbt.Keys.version

resolvers ++= Seq(
  Resolver.mavenLocal,
  DefaultMavenRepository
)

lazy val settings = Seq(
  organization := "com.github.ppotseluev", //TODO ?
  version := "1.0.0",
  scalaVersion := "2.13.3",
  scalaSource in Compile := baseDirectory.value / "src/main/scala",
  scalaSource in Test := baseDirectory.value / "src/test/scala",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "botgen",
    settings
//    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test" TODO
  ).aggregate(
  `botgen-model`,
  `botgen-bot`
)

lazy val `botgen-model` = project
  .settings(
    name := "botgen-model",
    settings,
    libraryDependencies ++= Seq(
      Dependency.tagging
//      Dependency.catsCore
    )//.map(_.withDottyCompat(scalaVersion.value))
  )

lazy val `botgen-bot` = project
  .settings(
    name := "botgen-bot",
    settings,
    libraryDependencies ++= Seq(
      Dependency.scalaGraph
    )//.map(_.withDottyCompat(scalaVersion.value))
  ).dependsOn(`botgen-model`)

lazy val `botgen-api` = project
  .settings(
    name := "botgen-api",
    settings,
    libraryDependencies ++= Seq(
      Dependency.circeCore,
      Dependency.circeGeneric,
      Dependency.tapirCore,
      Dependency.tapirJsonCirce,
      Dependency.tapirHttp4s
    )//.map(_.withDottyCompat(scalaVersion.value))
  ).dependsOn(`botgen-model`)

//todo DOT dep