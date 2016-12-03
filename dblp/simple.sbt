name := "Simple Project"

version := "1.0"

scalaVersion := "2.11.8"

fork := true


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.2",
  "org.apache.spark" %% "spark-graphx" % "2.0.2"
)
