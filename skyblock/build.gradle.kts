repositories {
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    implementation(project(":core"))
    compileOnly("com.destroystokyo:paperspigot:1.12.2")
    implementation("com.github.cryptomorin:XSeries:7.8.0")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("com.bgsoftware:bgsoftware:b196")
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