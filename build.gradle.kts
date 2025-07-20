plugins {
    kotlin("jvm") version "2.1.10"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "com.santo.rinha"
version = "1.0.0"

application {
    mainClass.set("com.santo.rinha.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core:2.3.8")
    implementation("io.ktor:ktor-server-netty:2.3.8")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")

    // Ktor client
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-cio:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.46.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.46.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.46.0")

    // PostgreSQL JDBC driver
    implementation("org.postgresql:postgresql:42.6.0")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Optional logging (can remove later)
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host:2.3.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.10")
}

kotlin {
    jvmToolchain(17)
}
