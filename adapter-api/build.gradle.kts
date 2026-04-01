plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.21"
    application
    id("com.gradleup.nmcp")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":core-api"))
    implementation(project(":transport-api"))
    api(libs.bundles.kotlinxEcosystem)
    api(libs.bundles.logging)
    api(libs.bundles.kotlinxSerialization)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(project(":driver-api"))

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.testSuit)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

val gitCommit = gitCommitShort().get()

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Kokoroid Adapter API",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "dev.kokoroidkt",
            "Git-Hash" to gitCommit,
            "Add-Opens" to "java.base/java.lang java.base/jdk.internal.loader",
            "Add-Exports" to "java.base/jdk.internal.loader",
            "Enable-Native-Access" to "ALL-UNNAMED",
        )
    }
    archiveFileName.set("kokoroid-adapter-api-$version-$gitCommit.jar")
}
