name := "htmlFinder"

version := "0.1"

scalaVersion := "2.12.6"

lazy val htmlFinder = (project in file(".")).
  settings(

    //mainClass in(Compile, packageBin) := Some("com.agileengine.xml.HTMLFinder"),
    mainClass in assembly := Some("com.rybak.htmlfinder.HTMLFinder"),

    assemblyJarName in assembly := "htmlFinder.jar",

    //credentials += Credentials(Path.userHome / ".sbt" / "credentials"),

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.jcenterRepo
    ),

    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    ),

    //util dependencies
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.11.2",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "org.slf4j" % "slf4j-simple" % "1.6.4"
    ),

    fork in Test := true,
    parallelExecution in Test := false,
    // exportJars := true,
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
  )