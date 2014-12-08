import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import scoverage.ScoverageSbtPlugin.instrumentSettings
import org.scoverage.coveralls.CoverallsPlugin.coverallsSettings

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

organization := "com.rayslava"

name := "scbnc"

version := "0.1"

scalaVersion := "2.11.4"

packageArchetype.java_application

instrumentSettings

coverallsSettings

ScoverageKeys.excludedPackages in ScoverageCompile := "<empty>;com.rayslava.scbnc.package;com.rayslava.MyActor"

ScoverageKeys.minimumCoverage := 75

ScoverageKeys.failOnMinimumCoverage := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.7",
  "org.mockito" % "mockito-core" % "1.10.8",
  "org.specs2" %% "specs2" % "2.4.11" % "test"
)
