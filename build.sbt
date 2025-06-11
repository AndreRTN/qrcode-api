ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.14" // switch to stable Scala 2

lazy val root = (project in file("."))
  .settings(
    name := "qrcode-api",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % "2.8.4",
      "org.json4s" %% "json4s-jackson" % "4.0.7",
      "org.eclipse.jetty" % "jetty-webapp" % "11.0.24",
      "javax.servlet" % "javax.servlet-api" % "4.0.1" % "provided",
      "com.google.zxing" % "core" % "3.5.3",
      "com.google.zxing" % "javase" % "3.5.3",
      "ch.qos.logback" % "logback-classic" % "1.5.11" % "runtime"
    )
  )
