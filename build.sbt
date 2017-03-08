name := "zkb4s"

organization := "org.red"

version := "1.0.5"

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-deprecation", "-feature")

publishTo := Some("Artifactory Realm" at "http://maven.red.greg2010.me/artifactory/sbt-local")
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.jcenterRepo

val scalazVersion = "7.2.9"
val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)