name := "ros-scala"

organization := "fr.laas.fape"

scalaVersion := "2.12.3"

version := "0.1.0"

// could be as well /opt/ros/hydro/share/maven/
resolvers += "ROS Java" at "https://github.com/rosjava/rosjava_mvn_repo/raw/master"

resolvers += "JCenter" at "http://jcenter.bintray.com"

//resolvers += "ROS WS" at "file://" + sys.env("ROS_MAVEN_DEPLOYMENT_REPOSITORY")

libraryDependencies += "org.ros.rosjava_core" % "rosjava" % "0.3.5" //"[0.1,)"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "latest.integration"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies ++= Seq(
  "org.reflections" % "reflections" % "0.9.9",
  "com.google.code.findbugs" % "jsr305" % "2.0.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
  "com.chuusai" %% "shapeless" % "2.3.2"
//  "org.slf4j" % "slf4j-api" % "1.7.5"
)