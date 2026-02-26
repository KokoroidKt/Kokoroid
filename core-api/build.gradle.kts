plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
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
    implementation(libs.koin.core)
    testImplementation(libs.koin.test)
    testImplementation(libs.bundles.testSuit)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
