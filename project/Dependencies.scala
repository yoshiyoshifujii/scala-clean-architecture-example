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
  val codec = "commons-codec" % "commons-codec" % version
}

object ScalaDDDBase {
  val version = "1.0.13"
  val core    = "com.github.j5ik2o" %% "scala-ddd-base-core" % version
}

