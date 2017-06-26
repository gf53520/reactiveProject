name := "reactiveProject"

version := "1.0"

resolvers ++= Seq("Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "aliyun Repository" at "http://maven.aliyun.com/nexus/content/groups/public/",
  "central Repository" at "http://repo1.maven.org/maven2")

libraryDependencies ++= {
  val akkaVersion = "2.4.14"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
    "com.typesafe.akka" % "akka-http-core_2.11" % "10.0.8",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.twitter" %% "util-collection" % "6.42.0",
    "commons-io" % "commons-io" % "2.4",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.yammer.metrics" % "metrics-core" % "2.2.0"
  )
}

