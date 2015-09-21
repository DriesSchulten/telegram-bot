name := "telegram-bot"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.14"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.14"
libraryDependencies += "io.spray" %% "spray-client" % "1.3.3"
libraryDependencies += "io.spray" %% "spray-httpx" % "1.3.3"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"
