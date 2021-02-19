dependencies {
    implementation(project(":core"))
    compileOnly("com.destroystokyo:paperspigot:1.12.2")
    implementation("com.github.cryptomorin:XSeries:7.8.0")
}

tasks {
    processResources {
        val tokens = mapOf("version" to project.version)
        from(sourceSets["main"].resources.srcDirs) {
            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokens)
        }
    }

    shadowJar {
        archiveFileName.set("${project.rootProject.name}-${project.name}-v${project.version}.jar")
        destinationDir = file("$rootDir/output")
    }
}