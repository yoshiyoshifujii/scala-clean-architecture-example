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
  .disablePlugins(WixMySQLPlugin)

lazy val `entities` = (project in file("modules/entities"))
  .settings(
    name := s"$baseName-entities",
    libraryDependencies ++= Seq(
      ScalaDDDBase.core,
      TypeLevel.core,
      TypeLevel.free,
      Beachape.enumeratum
    )
  )
  .settings(coreSettings)
  .dependsOn(infrastructure)
  .disablePlugins(WixMySQLPlugin)

lazy val `usecases` = (project in file("modules/usecases"))
  .settings(
    name := s"$baseName-usecases"
  )
  .settings(coreSettings)
  .dependsOn(entities)
  .disablePlugins(WixMySQLPlugin)

lazy val `adapters` = (project in file("modules/adapters"))
  .settings(
    name := s"$baseName-adapters",
    libraryDependencies ++= Seq(
      AkkaHttp.http,
      AkkaHttp.testkit % Test,
      Akka.stream,
      Heikoseeberger.circe,
      Circe.generic,
      Circe.parser,
      AirFrame.airframe,
      TypeLevel.effect,
      ScalaDDDBase.slick,
      MySQL.java,
      Monix.task
    )
  )
  .settings(coreSettings)
  .settings(adaptersSettings)
  .settings(
    generateAll in generator := Def
      .taskDyn {
        val ga = (generateAll in generator).value
        Def
          .task {
            (wixMySQLStop in flyway).value
          }
          .map(_ => ga)
      }
      .dependsOn(flywayMigrate in flyway)
      .value
  )
  .dependsOn(usecases)

lazy val `flyway` = (project in file("tools/flyway"))
  .settings(
    name := s"$baseName-flyway",
    libraryDependencies ++= Seq(
      MySQL.java
    )
  )
  .settings(coreSettings)
  .settings(flywaySettings)

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
  .disablePlugins(WixMySQLPlugin)
