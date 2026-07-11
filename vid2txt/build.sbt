// import Dependencies._

// ThisBuild / scalaVersion     := "2.13.16"
// ThisBuild / version          := "0.1.0-SNAPSHOT"
// ThisBuild / organization     := "com.example"
// ThisBuild / organizationName := "example"

// lazy val root = (project in file("."))
//   .settings(
//     name := "vid2txt",
//     libraryDependencies += munit % Test
    
//   )

// // See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

// build.sbt

//-------------------------------------------------------------------------------------------------------------------//

// Set the Scala version. Spark 3.5.0 runs on Scala 2.13.8. This is very important!
val scala213Version = "2.12.18"

lazy val root = project
  .in(file("."))
  .settings(
    name := "BrandToneAnalysis",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala213Version,

    // This section tells SBT what libraries to download
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.1.1" % "provided",
      "org.apache.spark" %% "spark-sql"  % "3.1.1" % "provided"
    )
  )

// This setting helps resolve some dependency conflicts
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "always"