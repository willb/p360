name := "patient360"

organization := "com.redhat.et"

version := "0.0.1"

val SPARK_VERSION = "1.6.0"

scalaVersion := "2.10.6"

resolvers += "Will's bintray" at "https://dl.bintray.com/willb/maven/"

def commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % SPARK_VERSION % "provided",
    "org.apache.spark" %% "spark-sql" % SPARK_VERSION % "provided",
    "org.apache.spark" %% "spark-mllib" % SPARK_VERSION % "provided",
    "com.redhat.et" %% "silex" % "0.0.10",
    "joda-time" % "joda-time" % "2.7", 
    "org.joda" % "joda-convert" % "1.7"
  )
)

seq(commonSettings:_*)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

lazy val patient360 = project in file(".")

lazy val spark = project.dependsOn(patient360)
  .settings(commonSettings:_*)
  .settings(
    name := "spark",
    publishArtifact := false,
    publish := {},
    initialCommands in console := """
      |import org.apache.spark.SparkConf
      |import org.apache.spark.SparkContext
      |import org.apache.spark.SparkContext._
      |import org.apache.spark.rdd.RDD
      |val app = new com.redhat.et.silex.app.ConsoleApp()
      |val spark = app.context
      |com.redhat.et.silex.util.logging.consoleLogWarn
    """.stripMargin,
    cleanupCommands in console := "spark.stop")
