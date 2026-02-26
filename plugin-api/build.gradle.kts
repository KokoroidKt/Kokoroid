plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.21"
    application
}

group = "dev.kokoroidkt"
version = project.findProperty("version") as String? ?: "undefined"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api(libs.bundles.kotlinxEcosystem)
    api(libs.bundles.logging)
    api(libs.bundles.kotlinxSerialization)
    implementation(platform(libs.koin.bom))
    api(project(":core-api"))
    implementation(libs.koin.core)
    testImplementation(libs.koin.test)
    testImplementation(libs.bundles.testSuit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
