name := "ros-scala"

organization := "fr.laas.fape"

version := "0.2-SNAPSHOT"

// could be as well /opt/ros/hydro/share/maven/
resolvers += "ROS Java" at "https://github.com/rosjava/rosjava_mvn_repo/raw/master"

resolvers += "ROS WS" at "file://" + sys.env("ROS_MAVEN_DEPLOYMENT_REPOSITORY")

libraryDependencies += "org.ros.rosjava_core" % "rosjava" % "0.2.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.9"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies ++= Seq(
  "org.reflections" % "reflections" % "0.9.9", // brings in a lot of dependencies
  "com.google.code.findbugs" % "jsr305" % "2.0.2"
//  "org.slf4j" % "slf4j-api" % "1.7.5"
)