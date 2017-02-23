name := "zkb4s"

organization := "org.red"

version := "1.0.0"

scalaVersion := "2.12.1"

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false
pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/greg2010/zkb4s</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:greg2010/zkb4s.git</url>
      <connection>scm:git@github.com:greg2010/zkb4s.git</connection>
    </scm>
    <developers>
      <developer>
        <id>greg2010</id>
        <name>greg2010</name>
        <url>N/A</url>
      </developer>
    </developers>)

val http4sVersion = "0.15.5a"
val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
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