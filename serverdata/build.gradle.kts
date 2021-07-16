plugins {
    java
}

group = "zone.themcgamer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":commons"))
    implementation("redis.clients:jedis:3.6.1")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("mysql:mysql-connector-java:8.0.23")
    testCompile("junit", "junit", "4.12")
}
