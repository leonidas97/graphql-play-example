name := """graphql-play-api"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.1",
  "org.sangria-graphql" %% "sangria" % "2.0.1",
  "org.webjars" %% "webjars-play" % "2.8.0-1",
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars.npm" % "react" % "17.0.1",
  "org.webjars.npm" % "react-dom" % "17.0.1",
  "org.webjars.npm" % "babel-standalone" % "6.26.0",
)
