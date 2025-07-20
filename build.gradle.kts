plugins {
    kotlin("jvm") version "2.1.10"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "com.santo"
version = "1.0.0"

application {
    mainClass.set("com.santo.rinha.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor core
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.46.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.46.0")

    // PostgreSQL JDBC driver
    implementation("org.postgresql:postgresql:42.6.0")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Logging (removível depois)
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Testes
    testImplementation("io.ktor:ktor-server-test-host:2.3.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.10")
}
