import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import scoverage.ScoverageSbtPlugin.instrumentSettings
import org.scoverage.coveralls.CoverallsPlugin.coverallsSettings

organization := "com.rayslava"

name := "scbnc"

version := "0.1"

scalaVersion := "2.11.2"

packageArchetype.java_application

instrumentSettings

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4",
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.specs2" %% "specs2" % "2.4" % "test"
)
