package buildsrc.convention

import org.gradle.api.Project
import org.gradle.api.provider.Provider

fun Project.gitCommitShort(length: Int = 7): Provider<String> =
    providers
        .exec {
            commandLine("git", "rev-parse", "--short=$length", "HEAD")
        }.standardOutput.asText
        .map { it.trim() }
