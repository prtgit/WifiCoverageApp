name := "androidtam"
 
version := "1.0" 
      
lazy val `androidtam` = (project in file(".")).enablePlugins(PlayJava)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

libraryDependencies ++= Seq( javaJdbc , cache , javaWs )
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.20.0"
unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )


fork in run := false
resolvers += "SQLite-JDBC Repository" at "https://oss.sonatype.org/content/repositories/snapshots"
      