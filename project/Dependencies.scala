import sbt._

object ScalaTest {
  val version        = "3.0.5"
  val core: ModuleID = "org.scalatest" %% "scalatest" % version
}

object Passay {
  val version = "1.3.0"
  val passay  = "org.passay" % "passay" % version
}

object Commons {
  val version = "1.11"
  val codec   = "commons-codec" % "commons-codec" % version
}

object ScalaDDDBase {
  val version = "1.0.14"
  val core = "com.github.j5ik2o" %% "scala-ddd-base-core" % version excludeAll ExclusionRule(
    organization = "org.typelevel"
  )
}

object TypeLevel {
  val version = "1.5.0"
  val core    = "org.typelevel" %% "cats-core" % version
  val free    = "org.typelevel" %% "cats-free" % version
}

object AkkaHttp {
  val version = "10.1.7"
  val http    = "com.typesafe.akka" %% "akka-http" % version
}

object Akka {
  val version = "2.5.19"
  val stream  = "com.typesafe.akka" %% "akka-stream" % version
}

object Heikoseeberger {
  val version = "1.24.3"
  val circe   = "de.heikoseeberger" %% "akka-http-circe" % version
}
