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

// source
Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala"
Test / scalaSource := baseDirectory.value / "src" / "test" / "scala"


// resources
Compile / resourceDirectory := baseDirectory.value / "src" / "main" / "resources"
Test / resourceDirectory := baseDirectory.value / "src" / "test" / "resources"