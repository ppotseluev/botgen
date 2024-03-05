import sbt.Keys.version

resolvers ++= Seq(
  Resolver.mavenLocal,
  DefaultMavenRepository
)

val repsy = "Repsy Managed Repository" at "https://repo.repsy.io/mvn/ppotseluev/default"

lazy val settings = Seq(
  organization := "com.github.ppotseluev",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.3",
  scalaSource in Compile := baseDirectory.value / "src/main/scala",
  scalaSource in Test := baseDirectory.value / "src/test/scala",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions := Seq(
    "-Ymacro-annotations",
    "-language:higherKinds",
    "-Xfatal-warnings",
    "-deprecation"
  ),
  ThisBuild / publishTo := Some(repsy),
  ThisBuild / credentials += Credentials(
    "Repsy Managed Repository",
    "repo.repsy.io", "ppotseluev",
    sys.env.getOrElse("REPSY_PWD", "UNDEFINED")
  ),
  ThisBuild / resolvers ++= List(Resolver.mavenLocal, repsy),
  addCompilerPlugin(Dependency.kindProjector),
  assemblyMergeStrategy in assembly := {
    case x if x.contains("io.netty.versions.properties") => MergeStrategy.concat
    case PathList(ps@_*) if ps.last endsWith "pom.properties" => MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "botgen",
    settings
  ).aggregate(
  `botgen-model`,
  `botgen-bot`,
  `botgen-core`,
  `botgen-api`
)

lazy val `botgen-model` = project
  .settings(
    name := "botgen-model",
    settings,
    libraryDependencies ++= Seq(
      Dependency.tagging
    )
  )

lazy val `botgen-bot` = project
  .settings(
    name := "botgen-bot",
    settings,
    libraryDependencies ++= Seq(
      Dependency.scalaGraph,
      Dependency.catsCore,
      Dependency.catsFree
    )
  ).dependsOn(`botgen-model`)

lazy val `botgen-core` = project
  .settings(
    name := "botgen-core",
    settings,
    libraryDependencies ++= Seq(
      Dependency.sttpClientCore,
      Dependency.sttpClientCirce,
      Dependency.circeCore,
      Dependency.circeGeneric,
      Dependency.circeGenericExtras,
      Dependency.doobieCore
    )
  ).dependsOn(`botgen-bot`)

lazy val `botgen-server` = project
  .settings(
    name := "botgen-server",
    settings,
    libraryDependencies ++= Seq(
      Dependency.sttpClientCatsBackend,
      Dependency.mysqlConnector,
      Dependency.typesafeConfig,
      Dependency.ficus,
      Dependency.scalaBcrypt
    )
  ).dependsOn(`botgen-core`)

lazy val `botgen-api` = project
  .settings(
    name := "botgen-api",
    settings,
    mainClass in assembly := Some("botgen.api.Main"),
    libraryDependencies ++= Seq(
      Dependency.tapirCore,
      Dependency.tapirJsonCirce,
      Dependency.tapirHttp4s,
      Dependency.tapirSwaggerUi,
      Dependency.tapirOpenapiDocs,
      Dependency.tapirOpenapiCirceYaml,
      Dependency.scalaLogging,
      Dependency.logback
    )
  ).dependsOn(`botgen-server`)