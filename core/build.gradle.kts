import buildsrc.convention.gitCommitShort

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.3.1"
    id("com.palantir.git-version")
    kotlin("plugin.serialization") version "2.1.21"
    application
}

application {
    mainClass.set("dev.kokoroidkt.MainKt")
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

group = "dev.kokoroidkt"
version = project.findProperty("version") as String? ?: "undefined"

val gitCommit = gitCommitShort().get()

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.1.0")

    api(libs.bundles.kotlinxEcosystem)
    api(libs.bundles.logging)
    api(libs.bundles.kotlinxSerialization)
    implementation(libs.bundles.exposedPlatform)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(libs.koin.test)

    implementation(project(":transport-api"))
    implementation(project(":core-api"))
    implementation(project(":plugin-api"))
    implementation(project(":adapter-api"))
    implementation(project(":driver-api"))
    implementation(project(":transport-api"))

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.testSuit)
    // testImplementation()
}

kotlin {
    jvmToolchain(21)
}

tasks.register<Delete>("deleteTestConfigFolder") {
    group = "cleanup"
    description = "Delete test configuration folder for test"
    delete("kokoroid")
}

tasks.test {
    dependsOn(":test-extension:jar")
    dependsOn(":core:deleteTestConfigFolder")
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "dev.kokoroidkt.core.MainKt",
            "Implementation-Title" to "Kokoroid Core",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "dev.kokoroidkt",
            "Git-Hash" to gitCommit,
            "Add-Opens" to "java.base/java.lang java.base/jdk.internal.loader",
            "Add-Exports" to "java.base/jdk.internal.loader",
            "Enable-Native-Access" to "ALL-UNNAMED",
        )
        archiveFileName.set("kokoroid-core-$version-$gitCommit-all.jar")
    }
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "dev.kokoroidkt.core.MainKt",
            "Implementation-Title" to "Kokoroid Core",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "dev.kokoroidkt",
            "Git-Hash" to gitCommit,
            "Add-Opens" to "java.base/java.lang java.base/jdk.internal.loader",
            "Add-Exports" to "java.base/jdk.internal.loader",
            "Enable-Native-Access" to "ALL-UNNAMED",
        )
    }
    archiveFileName.set("kokoroid-core-$version-$gitCommit.jar")
}
