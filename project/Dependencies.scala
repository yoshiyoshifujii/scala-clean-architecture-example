import sbt._

object ScalaTest {
  val version        = "3.0.5"
  val core: ModuleID = "org.scalatest" %% "scalatest" % version
}

object Passay {
  val version = "1.3.0"
  val passay  = "org.passay" % "passay" % version
}

object HashIds {
  val version = "1.0.3"
  val hashIds = "org.hashids" % "hashids" % version
}

object ScalaDDDBase {
  val version = "1.0.13"
  val core    = "com.github.j5ik2o" %% "scala-ddd-base-core" % version
}

object AkkaHttp {
  val version = "10.1.7"
  val http    = "com.typesafe.akka" %% "akka-http" % version
}

object Akka {
  val version = "2.5.19"
  val stream  = "com.typesafe.akka" %% "akka-stream" % version
}
