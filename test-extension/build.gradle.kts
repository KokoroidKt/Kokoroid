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
    implementation(project(":core-api"))
    implementation(project(":plugin-api"))
    implementation(project(":adapter-api"))
    implementation(project(":driver-api"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveBaseName.set("test-extension")
}

tasks.test {
    useJUnitPlatform()
}
