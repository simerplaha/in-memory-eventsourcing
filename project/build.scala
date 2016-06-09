import com.typesafe.sbt.SbtAspectj._
import sbt.Keys._
import sbt._


object Settings {
  val name = "in-memory-eventsourcing"

  val jvmRuntimeOptions = Seq(
    "-Xmx1G"
  )

  val version = "0.1.0"

  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  object versions {
    val akkaV = "2.4.7"

    val scala = "2.11.8"
    val log4js = "1.4.10"
  }



  val allDependencies =
    Def.setting(Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "com.github.mauricio" %% "postgresql-async" % "0.2.18",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "org.scalatest" %% "scalatest" % "3.0.0-M10" % "test",
      "org.specs2" %% "specs2-common" % "2.3.11" % "test",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
      "org.postgresql" % "postgresql" % "9.4.1207.jre7",
      "com.github.tminglei" %% "slick-pg" % "0.11.0",
      "com.zaxxer" % "HikariCP" % "2.4.3",
      "com.typesafe.akka" %% "akka-typed-experimental" % versions.akkaV,
      "com.typesafe.akka" %% "akka-agent" % versions.akkaV,
      "org.json4s" %% "json4s-native" % "3.2.10",
      "com.github.nscala-time" %% "nscala-time" % "2.8.0",
      //      "org.scalaz" %% "scalaz-core" % "7.2.1",
      "com.propensive" %% "rapture" % "2.0.0-M5",
      "io.argonaut" %% "argonaut" % "6.1",
      "com.github.alexarchambault" %% "argonaut-shapeless_6.1" % "1.0.0-RC1",
      "net.liftweb" %% "lift-json" % "3.0-M5-1",
      "net.liftweb" %% "lift-json-ext" % "3.0-M5-1"
    ))

  val macroDependencies =
    Def.setting(Seq(
      "org.scala-lang" % "scala-reflect" % versions.scala
    ))

}


object ApplicationBuild extends Build {

  scalaVersion in ThisBuild := "2.11.8"

  val commonSettings = Seq(
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
    )
  )

  lazy val eventsourcing = (project in file("eventsourcing"))
    .settings(
      name := "eventsourcing",
      version := Settings.version,
      scalaVersion := Settings.versions.scala,
      scalacOptions ++= Settings.scalacOptions,
      libraryDependencies ++= Settings.allDependencies.value,
      scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature",
        "-language:postfixOps", "-language:implicitConversions",
        "-language:higherKinds", "-language:existentials")
    )
    .dependsOn(macros)


  lazy val macros = (project in file("macros"))
    .settings(
      name := "macros",
      version := Settings.version,
      scalaVersion := Settings.versions.scala,
      scalacOptions ++= Settings.scalacOptions,
      scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature",
        "-language:postfixOps", "-language:implicitConversions",
        "-language:higherKinds", "-language:existentials"),
      libraryDependencies ++= Settings.macroDependencies.value
    )
}