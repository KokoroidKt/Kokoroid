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
    archiveFileName.set("kokoroid-plugin-api-$version.jar")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    archiveFileName.set("kokoroid-plugin-api-$version-sources.jar")
    from(sourceSets.main.get().allSource)
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaGeneratePublicationJavadoc) // 依赖生成文档的任务
    archiveClassifier.set("javadoc")
    archiveFileName.set("kokoroid-plugin-api-$version-javadoc.jar")
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
                name.set("Kokoroid Plugin API")
                description.set("Plugin API module of Kokoroid framework")
                artifactId = "kokoroid-plugin-api"
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
