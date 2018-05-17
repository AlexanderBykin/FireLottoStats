name := "FireLottoStats"

version := "1.0"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.9"
val slickVersion = "3.2.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC1",
  "org.postgresql" % "postgresql" % "42.2.1",
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.typesafe.slick" %% "slick-codegen" % slickVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.json4s" %% "json4s-native" % "3.5.3",
  "org.web3j" % "core" % "3.3.1"
)

assemblyJarName in assembly := "firelotto-stats.jar"

mainClass in assembly := Some("com.firelotto.stats.MainApp")

slick <<= slickCodeGenTask

//sourceGenerators in Compile += slickCodeGenTask

lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceDirectory, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = (dir / "main/scala").getPath
  val username = "postgres"
  val password = ""
  val url = "jdbc:postgresql://127.0.0.1:5432/firelottostats"
  val jdbcDriver = "org.postgresql.Driver"
  val slickDriver = "slick.jdbc.PostgresProfile"
  val pkg = "com.firelotto.stats.models"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, username, password), s.log))
  val fname = outputDir + "/models" + "/Tables.scala"
  Seq(file(fname))
}