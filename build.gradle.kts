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

allprojects {
    group = "dev.kokoroidkt"
    version = System.getenv("VERSION") ?: "undefined"
}
