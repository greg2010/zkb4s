name := "zkb4s"

organization := "org.red"

version := "1.0.1"

scalaVersion := "2.12.1"

publishTo := Some("greg2010-sbt-local" at "http://maven.red.greg2010.me/artifactory/sbt-local")
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.jcenterRepo

val http4sVersion = "0.15.5a"
val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.scalaz" %% "scalaz-core" % "7.2.9",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.http4s" %% "http4s-core" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)