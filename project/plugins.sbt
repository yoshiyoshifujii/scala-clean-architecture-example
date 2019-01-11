resolvers ++= Seq(
  "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/gateway.repositories/snapshots/",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/gateway.repositories/releases/",
  "Seasar Repository" at "http://maven.seasar.org/maven2/",
  "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"
)

addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")
