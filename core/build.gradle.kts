repositories {
    mavenCentral()
    maven {
        url = uri("http://repo.citizensnpcs.co/")
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":serverdata"))
    implementation("com.zaxxer:HikariCP:3.4.5")
    compileOnly("com.destroystokyo:paperspigot:1.12.2")
    implementation("com.github.cryptomorin:XSeries:7.8.0")
    implementation("com.warrenstrange:googleauth:1.4.0")
    implementation("com.google.zxing:javase:3.4.1")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.3")
    compileOnly("net.citizensnpcs:citizensapi:2.0.28-SNAPSHOT")
}