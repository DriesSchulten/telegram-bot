name := "telegram-bot"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io"
resolvers += "Akka repo" at "http://repo.akka.io/releases/"

val akkaVersion = "2.3.14"
val sprayVersion = "1.3.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-httpx" % sprayVersion,
  "io.spray" %% "spray-json" % "1.3.2",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)
