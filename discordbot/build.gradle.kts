dependencies {
    implementation(project(":core"))
    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("net.dv8tion:JDA:4.2.0_228")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "zone.themcgamer.discordbot.MGZBot"
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