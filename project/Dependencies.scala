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
  val version = "1.0.21"
  val core    = "com.github.j5ik2o" %% "scala-ddd-base-core" % version
  val slick   = "com.github.j5ik2o" %% "scala-ddd-base-slick" % version
}

object TypeLevel {
  val version = "1.5.0"
  val core    = "org.typelevel" %% "cats-core" % version
  val free    = "org.typelevel" %% "cats-free" % version
  val effect  = "org.typelevel" %% "cats-effect" % "1.2.0"
}

object Beachape {
  val version    = "1.5.13"
  val enumeratum = "com.beachape" %% "enumeratum" % version
}

object AkkaHttp {
  val version = "10.1.7"
  val http    = "com.typesafe.akka" %% "akka-http" % version
  val testkit = "com.typesafe.akka" %% "akka-http-testkit" % version
}

object Akka {
  val version = "2.5.19"
  val stream  = "com.typesafe.akka" %% "akka-stream" % version
}

object Heikoseeberger {
  val version = "1.24.3"
  val circe   = "de.heikoseeberger" %% "akka-http-circe" % version
}

object Circe {
  val version = "0.11.1"
  val generic = "io.circe" %% "circe-generic" % version
  val parser  = "io.circe" %% "circe-parser" % version
}

object AirFrame {
  val version  = "0.79"
  val airframe = "org.wvlet.airframe" %% "airframe" % version
}

object MySQL {
  val version = "5.1.42"
  val java    = "mysql" % "mysql-connector-java" % version
}

object Monix {
  val version = "3.0.0-RC2"
  val task    = "io.monix" %% "monix" % version
}
