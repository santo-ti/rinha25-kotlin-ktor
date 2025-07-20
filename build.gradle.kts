plugins {
    kotlin("jvm") version "1.9.23"
    application
}

application {
    mainClass.set("app.ApplicationKt")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
    kotlinOptions.freeCompilerArgs += listOf("-Xjvm-default=all")
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "-XX:+UseZGC",
        "-XX:+AlwaysPreTouch",
        "-XX:+UseNUMA",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseContainerSupport",
        "-XX:MaxRAMPercentage=80",
        "-XX:InitialRAMPercentage=80",
        "-XX:MinRAMPercentage=80",
        "-XX:+DisableExplicitGC",
        "-XX:+PerfDisableSharedMem",
        "-XX:+ParallelRefProcEnabled",
        "-XX:MaxInlineLevel=15",
        "-XX:MaxInlineSize=1024",
        "-XX:InlineSmallCode=1024",
        "-XX:ThreadStackSize=512",
        "-XX:+AggressiveOpts",
        "-XX:+OptimizeStringConcat",
        "-XX:+UseFastAccessorMethods",
        "-XX:+UseStringDeduplication",
        "-XX:+UseCompressedOops",
        "-XX:+UseCompressedClassPointers",
        "-XX:+ExitOnOutOfMemoryError",
        "-XX:ActiveProcessorCount=2"
    )
}
