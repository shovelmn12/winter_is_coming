plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // ktor
    implementation("io.ktor:ktor-server-core-jvm:2.2.4")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.4")
    implementation("io.ktor:ktor-server-websockets:2.2.4")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.2.4")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.2.4")

    // rx
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.6.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}