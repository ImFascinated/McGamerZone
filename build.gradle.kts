import java.util.Optional as javaOptional

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.4.30-RC"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.gorylenko.gradle-git-properties") version "2.2.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

allprojects {
    group = "zone.themcgamer"
    version = "1.0-SNAPSHOT"
}

subprojects {

    apply {
        plugin("java-library")
        plugin("kotlin")
        plugin("com.github.johnrengelman.shadow")
        plugin("com.gorylenko.gradle-git-properties")
        plugin("maven-publish")
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        compileOnly("org.jetbrains:annotations:20.1.0")

        // lombok
        compileOnly("org.projectlombok:lombok:1.18.16")
        annotationProcessor("org.projectlombok:lombok:1.18.16")

        testCompileOnly("org.projectlombok:lombok:1.18.16")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.16")

        compileOnly("org.slf4j:slf4j-simple:1.7.30")

        implementation("com.google.code.gson:gson:2.8.5")

        implementation("commons-io:commons-io:2.6")

        implementation("com.squareup.okhttp3:okhttp:4.10.0-RC1")
    }

    gitProperties {
        customProperty("insane_module", name)
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
            options.forkOptions.executable = "javac"
            options.encoding = "UTF-8"
        }

        compileKotlin {
            kotlinOptions.jvmTarget = "11";
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = project.name
                url = uri("https://mvn.cnetwork.club/repository/${project.name}/")

                credentials {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }

        getPropertySafe("vcsImcPrivateToken").ifPresent { token ->
            repositories {
                maven {
                    url = uri("https://vcs.cnetwork.club/api/v4/projects/1/packages/maven")
                    credentials(HttpHeaderCredentials::class) {
                        name = "Private-Token"
                        value = token // the variable resides in ~/.gradle/gradle.properties
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()

        maven {
            url = uri("https://mvn.cnetwork.club/repository/public/")

            credentials {
                username = getEnv("NEXUS_USERNAME").orElseGet {
                    getPropertySafe("mavenUsername")
                        .orElseThrow { IllegalArgumentException("Central repo not configured") }
                }
                password = getEnv("NEXUS_PASSWORD").orElseGet {
                    getPropertySafe("mavenPassword")
                        .orElseThrow { IllegalArgumentException("Central repo not configured") }
                }
            }

        }
    }
}

repositories {
    mavenCentral()
}

fun getEnv(env: String): java.util.Optional<String> {
    return javaOptional.ofNullable(System.getenv(env))
}

fun getPropertySafe(property: String): javaOptional<String> {
    return if (project.hasProperty(property)) javaOptional.of(
        project.property(property).toString()
    ) else javaOptional.empty()
}
