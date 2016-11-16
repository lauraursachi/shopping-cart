import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "com.lursachi",
  version := "1.0",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "shopping-cart",

    libraryDependencies ++= {

      Seq(
        "com.typesafe" % "config" % "1.3.0",
        "com.typesafe.akka" %% "akka-stream" % "2.4.10",
        "com.typesafe.akka" %% "akka-http-core" % "2.4.10",
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10",
        "io.spray" %% "spray-json" % "1.3.2",
        "org.scalatest" %% "scalatest" % "3.0.0" % Test,
        "com.typesafe.akka" %% "akka-http-testkit" % "2.4.10" % Test,
        "com.typesafe.akka" %% "akka-testkit" % "2.4.10" % Test

      )

    }
  )
    