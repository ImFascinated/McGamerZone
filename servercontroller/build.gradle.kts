repositories {
    jcenter()
}

dependencies {
    api(project(":serverdata"))
    implementation("com.mattmalec.Pterodactyl4J:Pterodactyl4J:2.BETA_24")
    implementation("com.hierynomus:sshj:0.30.0")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "zone.themcgamer.controller.ServerController"
    }
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