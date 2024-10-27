name := "libcairo"

version := "0.0.7"

versionScheme := Some("early-semver")

scalaVersion := "3.5.2"

enablePlugins(ScalaNativePlugin)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials",
)

organization := "io.github.edadma"

githubOwner := "edadma"

githubRepository := name.value

Global / onChangedBuildSource := ReloadOnSourceChanges

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.githubPackages("edadma")

//libraryDependencies += "io.github.edadma" %%% "freetype" % "0.0.1"
libraryDependencies += "io.github.edadma" %%% "freetype_face" % "0.0.2"

licenses := Seq("ISC" -> url("https://opensource.org/licenses/ISC"))

homepage := Some(url("https://github.com/edadma/" + name.value))

publishMavenStyle := true

Test / publishArtifact := false
