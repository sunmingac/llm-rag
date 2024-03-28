val scala3Version = "3.4.0"
val http4sVersion = "0.23.26"
val circeVersion = "0.14.7"
val catsEffectVersion = "3.3.12"
val fs2Version = "3.10.2"
val logbackVersion = "1.5.6"
val munitCatsEffectVersion = "1.0.7"
val munitVersion = "0.7.29"
val catsEffectCpsVersion = "0.4.0"
val kyoVersion = "0.9.3"
val sttpVersion = "3.9.6"
val catsVersion = "2.10.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "rag",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= dependencies
  )

val dependencies = Seq(
  "org.scalameta" %% "munit" % munitVersion % Test,
  // "core" module - IO, IOApp, schedulers
  // This pulls in the kernel and std modules automatically.
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
  "org.typelevel" %% "cats-effect-kernel" % catsEffectVersion,
  // standard "effect" library (Queues, Console, Random etc.)
  "org.typelevel" %% "cats-effect-std" % catsEffectVersion,
  "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectVersion % Test,
//  "org.typelevel" %% "cats-effect-cps" % catsEffectCpsVersion,
  // available for 2.12, 2.13, 3.2
  "co.fs2" %% "fs2-core" % fs2Version,
  // optional I/O library
  "co.fs2" %% "fs2-io" % fs2Version,
  // optional reactive streams interop
  "co.fs2" %% "fs2-reactive-streams" % fs2Version,
  // optional scodec interop
  "co.fs2" %% "fs2-scodec" % fs2Version,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "io.getkyo" %% "kyo-core" % kyoVersion,
  "io.getkyo" %% "kyo-direct" % kyoVersion,
  "io.getkyo" %% "kyo-cache" % kyoVersion,
  "io.getkyo" %% "kyo-stats-otel" % kyoVersion,
  "io.getkyo" %% "kyo-sttp" % kyoVersion,
  "io.getkyo" %% "kyo-tapir" % kyoVersion,
  "com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
)



Compile / run / fork := true