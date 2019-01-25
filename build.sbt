import Settings._

val baseName = "scala-clean-architecture-example"

lazy val `infrastructure` = (project in file("modules/infrastructure"))
  .settings(
    name := s"$baseName-infrastructure",
    libraryDependencies ++= Seq(
      Passay.passay,
      Commons.codec
    )
  )
  .settings(coreSettings)

lazy val `entities` = (project in file("modules/entities"))
  .settings(
    name := s"$baseName-entities",
    libraryDependencies ++= Seq(
      ScalaDDDBase.core,
      TypeLevel.core,
      TypeLevel.free
    )
  )
  .settings(coreSettings)
  .dependsOn(infrastructure)

lazy val `usecases` = (project in file("modules/usecases"))
  .settings(
    name := s"$baseName-usecases"
  )
  .settings(coreSettings)
  .dependsOn(entities)

lazy val `adapters` = (project in file("modules/adapters"))
  .settings(
    name := s"$baseName-adapters",
    libraryDependencies ++= Seq(
      AkkaHttp.http,
      Akka.stream,
      Heikoseeberger.circe
    )
  )
  .settings(coreSettings)
  .dependsOn(usecases)

lazy val `main` = (project in file("modules/main"))
  .settings(
    name := s"$baseName-main"
  )
  .settings(coreSettings)
  .dependsOn(adapters)

lazy val `root` = (project in file("."))
  .settings(
    name := baseName
  )
  .settings(coreSettings)
  .aggregate(
    entities,
    usecases,
    adapters,
    main
  )
