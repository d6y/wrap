version := "1.0.0"

name := "wrap"

scalaVersion := "2.11.7"

libraryDependencies ++= cats ++ logging ++ testlib ++ parser

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Xlint",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

lazy val cats = Seq("org.spire-math" %% "cats" % "0.3.0")

lazy val logging = Seq("com.typesafe.scala-logging" %% "scala-logging" % "3.1.0")

lazy val testlib = Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

lazy val parser = Seq(
  "org.tpolecat" %% "atto-core"  % "0.4.2"
)
