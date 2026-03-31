import org.gradle.kotlin.dsl.repositories
plugins {
    alias(libs.plugins.ktlint)
}

repositories {
    // Required to download KtLint
    mavenCentral()
}
ktlint {
    version = "1.4.0"
}
