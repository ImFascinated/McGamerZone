repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    api(project(":serverdata"))
    compileOnly("io.github.waterfallmc:waterfall-api:1.16-R0.4-SNAPSHOT")
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