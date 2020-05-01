name := "forex_test"

version := "0.1"

scalaVersion := "2.13.2"

val akkaVersion = "2.6.4"
val akkaHttpVersion = "10.1.11"
val playJsonVersion = "2.8.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.play" %% "play-json" % playJsonVersion

)