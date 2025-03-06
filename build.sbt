import org.scalajs.linker.interface.ModuleSplitStyle
import sbt.Keys.scalaVersion
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.learn"
ThisBuild / scalaVersion := "3.6.2"

lazy val scala3Version = "3.6.2"
lazy val learnPackage = "com.learn"
lazy val tapirVersion = "1.11.15"
//lazy val tapirVersion = "1.11.13"
lazy val circeVersion = "0.14.0"
lazy val circeOpenApiVersion = "0.11.7"
lazy val tyrianVersion = "0.6.1"
lazy val fs2DomVersion = "0.1.0"

lazy val rootProject = (project in file("."))
  .settings(
    Seq(
      name := "Scala3TapirFullstack",
      version := "0.1.0-SNAPSHOT",
      organization := "com.learn",
      scalaVersion := scala3Version,
    )
  )
  .aggregate(server)

lazy val common = (crossProject(JSPlatform, JVMPlatform) in file("common"))
  .settings(
    name         := "common",
    scalaVersion := scala3Version,
    organization := learnPackage
  )
  .jvmSettings(
    // add here if necessary
  )
  .jsSettings(
    // Add JS-specific settings here
  )

lazy val server = project.in(file("server"))
  .settings(
    name         := "server",
    scalaVersion := scala3Version,
    organization := learnPackage,
    Compile / run / mainClass := Some("com.learn.Main"),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-cats-effect" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-netty-server-cats" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.16",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "com.softwaremill.sttp.client3" %% "circe" % "3.10.2" % Test,

      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion,

      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % circeOpenApiVersion,
      // see https://github.com/softwaremill/sttp-apispec
      "com.softwaremill.sttp.apispec" %% "openapi-model" % "0.11.7",
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.11.7"
    )
  )
  .dependsOn(common.jvm)

lazy val client = project.in(file("./client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := scala3Version,
    Compile / run / fork := true,
    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "client" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("client"))
        )
    },

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "io.indigoengine" %%% "tyrian-io"     % tyrianVersion,
      "com.armanbilge"  %%% "fs2-dom"       % fs2DomVersion,
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "org.scalameta" %% "munit" % "1.0.0" % Test
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    semanticdbEnabled := true,
    autoAPIMappings   := true
  )
  .dependsOn(common.js)
