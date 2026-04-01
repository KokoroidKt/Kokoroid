import java.time.temporal.ChronoUnit

plugins {
    alias(libs.plugins.ktlint)

    id("com.gradleup.nmcp.aggregation").version("1.4.4")
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
    version = findProperty("version")?.toString()
        ?: System.getenv("VERSION")
        ?: "undefined"
}

nmcpAggregation {
    centralPortal {
        username = providers.environmentVariable("MAVEN_USERNAME").orNull
        password = providers.environmentVariable("MAVEN_PASSWORD").orNull

        // optional: publish manually from the portal
        publishingType = "AUTOMATIC"

        // optional: configure the name of your publication in the portal UI
        publicationName = "kokoroid:$version"
    }
}
