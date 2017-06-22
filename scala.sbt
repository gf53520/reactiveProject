scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")

scalacOptions ++= Seq("-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")

