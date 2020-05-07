val circeVersion = "0.12.1"
lazy val root = (project in file("."))
  .settings(
    name := "project0828",
    version := "0.1",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.8",
      "com.typesafe.akka" %% "akka-stream" % "2.5.23",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.9",


      // SQL generator
      "com.typesafe.slick" %% "slick" % "3.3.2",
      "org.slf4j" % "slf4j-nop" % "1.7.26",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
      "com.h2database" % "h2" % "1.4.196",

      // JSON serialization library
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,


      "de.heikoseeberger" %% "akka-http-circe" % "1.29.1", //FailFastCirceSupport

      //test
      "org.scalatest" %% "scalatest" % "3.0.8" % "test" ,
      "org.scalamock" %% "scalamock" % "4.4.0" % "test",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % "test",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.10" % "test",
      "org.mockito" %% "mockito-scala-scalatest" % "1.7.0" % "test"




    )


  )
