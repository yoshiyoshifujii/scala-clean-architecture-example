import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import sbt._
import sbt.Keys._
import scala.concurrent.duration._
import com.chatwork.sbt.wix.embedded.mysql.WixMySQLPlugin.autoImport._
import org.flywaydb.sbt.FlywayPlugin.autoImport._
import jp.co.septeni_original.sbt.dao.generator.SbtDaoGeneratorKeys._

object Settings {

  val dbDriver   = "com.mysql.jdbc.Driver"
  val dbName     = "scae"
  val dbUser     = "scae"
  val dbPassword = "passwd"
  val dbPort     = 3310
  val dbUrl      = s"jdbc:mysql://localhost:$dbPort/$dbName?useSSL=false"

  lazy val coreSettings = Seq(
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

  lazy val flywaySettings = Seq(
    wixMySQLVersion := com.wix.mysql.distribution.Version.v5_6_21,
    wixMySQLUserName := Some(dbUser),
    wixMySQLPassword := Some(dbPassword),
    wixMySQLSchemaName := dbName,
    wixMySQLPort := Some(dbPort),
    wixMySQLDownloadPath := Some(sys.env("HOME") + "/.wixMySQL/downloads"),
    wixMySQLTimeout := Some(2 minutes),
    flywayDriver := dbDriver,
    flywayUrl := dbUrl,
    flywayUser := dbUser,
    flywayPassword := dbPassword,
    flywaySchemas := Seq(dbName),
    flywayLocations := Seq(
      s"filesystem:${baseDirectory.value}/src/test/resources/2019-01-23/",
      s"filesystem:${baseDirectory.value}/src/test/resources/2019-01-23/test",
    ),
    flywayPlaceholderReplacement := true,
    flywayPlaceholders := Map(
      "engineName"                 -> "MEMORY",
      "idSequenceNumberEngineName" -> "MyISAM"
    ),
    parallelExecution in Test := false,
    flywayMigrate := (flywayMigrate dependsOn wixMySQLStart).value
  )

  lazy val adaptersSettings = Seq(
    parallelExecution in Test := false,
    driverClassName in generator := dbDriver,
    jdbcUrl in generator := dbUrl,
    jdbcUser in generator := dbUser,
    jdbcPassword in generator := dbPassword,
    propertyTypeNameMapper in generator := {
      case "INTEGER" | "TINYINT"             => "Int"
      case "BIGINT"                          => "Long"
      case "BIGINT UNSIGNED"                 => "Long"
      case "VARCHAR" | "TEXT"                => "String"
      case "BOOLEAN" | "BIT"                 => "Boolean"
      case "DATE" | "TIMESTAMP" | "DATETIME" => "java.time.ZonedDateTime"
      case "DECIMAL"                         => "BigDecimal"
      case "ENUM"                            => "String"
    },
    tableNameFilter in generator := { tableName: String =>
      (tableName.toUpperCase != "SCHEMA_VERSION") && !tableName.toUpperCase.endsWith("ID_SEQUENCE_NUMBER")
    },
    outputDirectoryMapper in generator := {
      case s if s.endsWith("Spec") => (sourceDirectory in Test).value
      case _ =>
        new java.io.File((scalaSource in Compile).value, "/adapters/storages/dao/jdbc")
    },
    templateNameMapper in generator := {
      case className if className.endsWith("Spec") => "template_spec.ftl"
      case _                                       => "template.ftl"
    },
    compile in Compile := ((compile in Compile) dependsOn (generateAll in generator)).value
  )
}
