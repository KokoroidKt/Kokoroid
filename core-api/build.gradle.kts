plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    application
    id("com.palantir.git-version")
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

val gitVersion: groovy.lang.Closure<String> by extra
val gitCommit = gitVersion().substring(0, 7)

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Kokoroid Core API",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "dev.kokoroidkt",
            "Git-Hash" to gitCommit,
            "Add-Opens" to "java.base/java.lang java.base/jdk.internal.loader",
            "Add-Exports" to "java.base/jdk.internal.loader",
            "Enable-Native-Access" to "ALL-UNNAMED",
        )
    }
    archiveFileName.set("kokoroid-core-api-$version-$gitCommit.jar")
}
