dependencies {
    api(project(":serverdata"))
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("mysql:mysql-connector-java:8.0.23")
    compile("com.sparkjava:spark-core:2.9.3")
    compile("com.google.guava:guava:30.1-jre")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "zone.themcgamer.api.API"
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