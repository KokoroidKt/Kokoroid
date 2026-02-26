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
    implementation(project(":core-api"))
    implementation(project(":transport-api"))
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    api(libs.bundles.kotlinxEcosystem)
    api(libs.bundles.logging)
    api(libs.bundles.kotlinxSerialization)

    testImplementation(libs.bundles.testSuit)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
