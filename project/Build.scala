import java.io.File
import sbt._
import Keys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import Process._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.site.JekyllSupport.Jekyll
import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtGit.git

object YinYangBuild extends Build {
  lazy val projectSettings = Seq[Setting[_]](
    version              := "0.2.0-SNAPSHOT",
    organization         := "ch.epfl.lamp",
    licenses             := Seq("New BSD" -> 
      url("https://raw.githubusercontent.com/scala-yinyang/scala-yinyang/master/LICENCE")),
    homepage             := Some(url("https://github.com/scala-yinyang/scala-yinyang")),
    organizationHomepage := Some(url("http://lamp.epfl.ch")),
    scmInfo              := Some(ScmInfo(
      url("https://github.com/scala-yinyang/scala-yinyang.git"),
      "scm:git:git://github.com/scala-yinyang/scala-yinyang.git"))
  )

  lazy val scalaSettings = Defaults.defaultSettings ++ Seq(
    scalaOrganization    := scalaOrg,
    scalaVersion         := "2.11.2",
    crossScalaVersions := Seq("2.11.0", "2.11.1", "2.11.2"),
    scalacOptions        := defaultScalacOptions
  )

  // libraries
  lazy val libraryDeps = Seq(
    libraryDependencies <<= scalaVersion(ver => Seq(
      scalaOrg % "scala-library" % ver,
      scalaOrg % "scala-reflect" % ver,
      scalaOrg % "scala-compiler" % ver,
      "org.scalatest" % "scalatest_2.11" % "2.1.5" % "test",
      "junit" % "junit" % "4.11" % "test",
      "EPFL" % "macro-lms_2.11" % "1.0.0-wip-macro", //local lms published
      "ch.epfl.lamp" %% "scala-records" % "0.1-cedric"
      // "org.scala-lang.virtualized" %% "scala-virtualized" % "1.0.0-macrovirt" //is this 
  )))

  // modules
  lazy val _yinyang      = Project(id = "root",             base = file(".")                   , settings = Project.defaultSettings ++ Seq(publishArtifact := false)) aggregate (yinyang, yy_core, yy_paradise, example_dsls)
  lazy val yy_core       = Project(id = "yinyang-core",     base = file("components/core")     , settings = defaults ++ Seq(name := "yinyang-core"))
  lazy val yy_paradise   = Project(id = "yinyang-paradise", base = file("components/paradise") , settings = defaults ++ paradise ++ Seq(name := "yinyang-paradise")) dependsOn(yy_core)
  lazy val yinyang       = Project(id = "scala-yinyang",    base = file("components/yin-yang") , settings = defaults ++ Seq(name := "scala-yinyang")) dependsOn(yy_core)  
  lazy val example_dsls  = Project(id = "example-dsls",     base = file("components/dsls")     , settings = defaults ++ Seq(publishArtifact := false)) dependsOn(yinyang)
  //lazy val delite        = Project(id = "delite-test",      base = file("components/delite-test"),settings = defaults ++ paradise ++ Seq(name := "delite-test")) dependsOn(yinyang) //, yy_core, yy_paradise, example_dsls)

  lazy val defaults = projectSettings ++ scalaSettings ++ formatSettings ++ libraryDeps ++ Seq(
    resolvers +=  "OSSH" at "https://oss.sonatype.org/content/groups/public",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    // paths - so we don't need to have src/main/scala ... just src/ test/ and resources/
    scalaSource in Compile <<= baseDirectory(_ / "src"),
    scalaSource in Test <<= baseDirectory(_ / "test"),
    resourceDirectory in Compile <<= baseDirectory(_ / "resources"),
    // sbteclipse needs some info on source directories:
    unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_)),
    unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_)),
    parallelExecution in Test := false,
    incOptions := incOptions.value.withNameHashing(true)
  )

  // add the macro paradise compiler plugin
  lazy val paradise = Seq(
    libraryDependencies += {
      val paradiseVersion =  
        if (scalaVersion.value == "2.11.2") "2.0.1"
        else"2.0.0"
      compilerPlugin("org.scalamacros" % "paradise" %  paradiseVersion cross CrossVersion.full)
    },
    scalacOptions := defaultScalacOptions
  )

  lazy val website = Seq(site.settings,
    ghpages.settings,
    site.includeScaladoc(),
    site.jekyllSupport(),
    git.remoteRepo := "git@github.com:scala-yinyang/scala-yinyang.git",
    includeFilter in Jekyll := ("*.html" | "*.png" | "*.js" | "*.css" | "CNAME")
  )

  lazy val publishing = Seq(
    // for integration testing against scala snapshots
    resolvers += Resolver.sonatypeRepo("snapshots"),
    // so we don't have to wait for maven central synch
    resolvers += Resolver.sonatypeRepo("releases"),
    // If we want on maven central, we need to be in maven style.
    publishMavenStyle := true,
    publishArtifact in Test := false,
    // The Nexus repo we're publishing to.
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    // Maven central cannot allow other repos.  We're ok here because the artifacts we
    // we use externally are *optional* dependencies.
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <developers>
        <developer>
          <id>vjovanov</id>
          <name>Vojin Jovanovic</name>
          <url>http://www.vjovanov.com/</url>
        </developer>
        <developer>
          <id>amirsh</id>
          <name>Amir Shaikhha</name>
          <url>http://people.epfl.ch/amir.shaikhha</url>          
        </developer>
        <developer>
          <id>sstucki</id>
          <name>Sandro Stucki</name>
          <url>http://people.epfl.ch/sandro.stucki</url>          
        </developer>        
      </developers>
    )
  )

  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
    .setPreference(RewriteArrowSymbols, false)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
  }
  lazy val defaultScalacOptions = Seq("-deprecation", "-feature", "-language:higherKinds", "-language:implicitConversions")
  lazy val scalaOrg = "org.scala-lang"
}
