plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.21"
    application
    id("com.palantir.git-version")
}

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
            "Implementation-Title" to "Kokoroid Plugin API",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "dev.kokoroidkt",
            "Git-Hash" to gitCommit,
            "Add-Opens" to "java.base/java.lang java.base/jdk.internal.loader",
            "Add-Exports" to "java.base/jdk.internal.loader",
            "Enable-Native-Access" to "ALL-UNNAMED",
        )
    }
    archiveFileName.set("kokoroid-plugin-api-$version-$gitCommit.jar")
}

configureMavenPublishFor("kokoroid-plugin-api")
