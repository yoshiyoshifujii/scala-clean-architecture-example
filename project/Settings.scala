import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import sbt._
import sbt.Keys._

object Settings {

  val coreSettings = Seq(
    organization := "com.github.yoshiyoshiifujii",
    scalaVersion := "2.12.8",
    scalacOptions ++= {
      Seq(
        "-feature",
        "-deprecation",
        "-unchecked",
        "-encoding",
        "UTF-8",
        "-language:_"
      ) ++ {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2L, scalaMajor)) if scalaMajor == 12 =>
            Seq.empty
          case Some((2L, scalaMajor)) if scalaMajor <= 11 =>
            Seq(
              "-Yinline-warnings"
            )
        }
      }
    },
    scalafmtOnCompile in ThisBuild := true,
    scalafmtTestOnCompile in ThisBuild := true,
    resolvers ++= Seq(
      "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/gateway.repositories/snapshots/",
      "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/gateway.repositories/releases/",
      "Seasar2 Repository" at "http://maven.seasar.org/maven2",
      Resolver.bintrayRepo("danslapman", "maven")
    ),
    libraryDependencies ++= Seq(
      ScalaTest.core % Test
    )
  )
}
