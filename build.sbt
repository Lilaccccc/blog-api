ThisBuild / version := "0.0.1"

ThisBuild / scalaVersion := "3.8.1"

lazy val root = (project in file("."))
  .settings(
    name := "blog-api",
    idePackagePrefix := Some("org.a"),
    javaOptions ++= Seq(
      "-Xms64m",
      "-Xmx64m"
    ),
    Global / bspEnabled := true,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
      "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % "1.13.8",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.13.8",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.13.8",
      "org.apache.pekko" %% "pekko-http-cors" % "1.3.0",
      "io.getkyo" %% "kyo-core" % "1.0-RC1",
      "io.getkyo" %% "kyo-prelude" % "1.0-RC1",
      "io.getkyo" %% "kyo-direct" % "1.0-RC1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.6",
      "ch.qos.logback" % "logback-classic" % "1.5.32",
      "redis.clients" % "jedis" % "7.3.0",
    )
  )
