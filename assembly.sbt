import AssemblyKeys._ // put this at the top of the file

assemblySettings

jarName in assembly := { s"${name.value}-${version.value}.jar" }

test in assembly := {}

