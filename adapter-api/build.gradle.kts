plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    application
    id("com.gradleup.nmcp")
    `maven-publish`
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.dokka-javadoc")
}

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
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
    archiveFileName.set("kokoroid-adapter-api-$version.jar")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    archiveFileName.set("kokoroid-adapter-api-$version-sources.jar")
    from(sourceSets.main.get().allSource)
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaGeneratePublicationJavadoc) // 依赖生成文档的任务
    archiveClassifier.set("javadoc")
    archiveFileName.set("kokoroid-adapter-api-$version-javadoc.jar")
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
}

tasks.build {
    dependsOn(sourcesJar)
    dependsOn(dokkaJavadocJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJavadocJar)
            pom {
                name.set("Kokoroid Adapter API")
                description.set("Adapter API module of Kokoroid framework")
                artifactId = "kokoroid-adapter-api"
                url.set("https://github.com/kokoroidkt/kokoroid")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License, version 2.1")
                        url.set("https://www.gnu.org/licenses/lgpl-2.1.txt")
                    }
                }
                developers {
                    developer {
                        id.set("kokoroidkt")
                        name.set("Kokoroid Dev Team")
                    }
                    developer {
                        id.set("moran0710")
                        name.set("Moran0710")
                        email.set("moran0710@qq.com")
                    }
                }
                scm {
                    url.set("https://github.com/kokoroidkt/kokoroid.git")
                    connection.set("scm:git:git://github.com/kokoroidkt/kokoroid.git")
                    developerConnection.set("scm:git:ssh://git@github.com/kokoroidkt/kokoroid.git")
                }
            }
        }
    }
}

autoConfigureSigning()
