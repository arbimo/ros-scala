name := "ros-scala"

scalaVersion := "2.10.4"

resolvers += "ROS Java" at "https://github.com/rosjava/rosjava_mvn_repo/raw/master"
//resolvers += "ROS Java" at "file:///opt/ros/hydro/share/maven/"
// could be as well /opt/ros/hydro/share/maven/

//lazy val ros_maven_ws := sys.env("ROS_MAVEN_DEPLOYMENT_REPOSITORY")

resolvers += "ROS WS" at "file://" + sys.env("ROS_MAVEN_DEPLOYMENT_REPOSITORY")

libraryDependencies += "org.ros.rosjava_core" % "rosjava" % "latest.integration" //"[0.1,)"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "latest.integration"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.4"

//libraryDependencies += "org.reflections" % "reflections" % "0.9.9"

// Provides the @Nullable annoation that created problems with Reflections
//libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.0"

libraryDependencies ++= Seq(
  "org.reflections" % "reflections" % "0.9.9",
  "com.google.code.findbugs" % "jsr305" % "2.0.2"
//  "org.slf4j" % "slf4j-api" % "1.7.5"
)