name := "ros-scala"

scalaVersion := "2.10.4"

resolvers += "ROS Java" at "https://github.com/rosjava/rosjava_mvn_repo/raw/master"
//resolvers += "ROS Java" at "file:///opt/ros/hydro/share/maven/"
// could be as well /opt/ros/hydro/share/maven/

//lazy val ros_maven_ws := sys.env("ROS_MAVEN_DEPLOYMENT_REPOSITORY")

resolvers += "ROS WS" at "file://" + sys.env("ROS_MAVEN_DEPLOYMENT_REPOSITORY")

libraryDependencies += "com.github.rosjava.myjava_messages" % "adream_actions" % "0.0.0"

libraryDependencies += "org.ros.rosjava_core" % "rosjava" % "latest.integration" //"[0.1,)"

libraryDependencies += "org.ros.rosjava_messages" % "move_base_msgs" % "latest.integration" //"1.11.14"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "latest.integration"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.4"

libraryDependencies += "org.reflections" % "reflections" % "0.9.9"


// If you need to specify main classes manually, use packSettings and packMain
//packSettings

// [Optional] Creating `hello` command that calls org.mydomain.Hello#main(Array[String]) 
//packMain := Map("listener" -> "org.ros.RosRun com.github.rosjava_catkin_package_a.my_pub_sub_tutorial.Listener", "publisher" -> "org.ros.RosRun")