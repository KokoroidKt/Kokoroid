import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.plugins.signing.SigningExtension

private fun Project.isPublishRequested(): Boolean =
    gradle.taskGraph.allTasks.any { task ->
        task.name.startsWith("publish", ignoreCase = true) ||
            task.name.contains("publish", ignoreCase = true) ||
            task.name.equals("sign", ignoreCase = true)
    }

private fun Project.requireEnv(varName: String): String =
    providers
        .environmentVariable(varName)
        .orNull
        ?.takeIf { it.isNotBlank() }
        ?: throw GradleException("$varName is required for publishing")

fun Project.configureMavenPublish(
    publishName: String = name,
    componentName: String = "java",
) {
    pluginManager.apply("maven-publish")
    pluginManager.apply("signing")

    tasks.withType(GenerateModuleMetadata::class.java).configureEach {
        enabled = false
    }

    afterEvaluate {
        val publishing = extensions.findByType(PublishingExtension::class.java) ?: return@afterEvaluate
        val signing = extensions.findByType(SigningExtension::class.java) ?: return@afterEvaluate
        val component = components.findByName(componentName) ?: return@afterEvaluate

        publishing.publications {
            if (findByName("mavenJava") == null) {
                create("mavenJava", MavenPublication::class.java) {
                    from(component)

                    pom {
                        name.set(publishName)
                        description.set("A awesome Bot Framework -- based by kotlinx.coroutines")
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
                                name.set("moran0710")
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

        publishing.repositories {
            maven {
                name = "CentralPortal"
                url = uri("https://central.sonatype.com/api/v1/publisher/")
            }
        }

        gradle.taskGraph.whenReady {
            if (!isPublishRequested()) return@whenReady

            val mavenUsername = requireEnv("MAVEN_USERNAME")
            val mavenPassword = requireEnv("MAVEN_PASSWORD")
            val gpgPrivateKey = requireEnv("GPG_PRIVATE_KEY")
            val gpgPassphrase = requireEnv("GPG_PASSPHRASE")

            publishing.repositories
                .withType(org.gradle.api.artifacts.repositories.MavenArtifactRepository::class.java)
                .named("CentralPortal")
                .configure {
                    credentials {
                        username = mavenUsername
                        password = mavenPassword
                    }
                }

            signing.useInMemoryPgpKeys(gpgPrivateKey, gpgPassphrase)
            signing.sign(publishing.publications)
        }
    }
}

fun Project.configureMavenPublish() {
    configureMavenPublish(publishName = name, componentName = "java")
}

fun Project.configureMavenPublishFor(
    publishName: String = name,
    componentName: String = "java",
) {
    configureMavenPublish(publishName = publishName, componentName = componentName)
}
