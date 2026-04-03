import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

/**
 * 自动配置 GPG 签名：
 * - 如果环境变量 GPG_PRIVATE_KEY 和 GPG_PASSPHRASE 存在，使用内存密钥（CI 环境）
 * - 否则回退到本地 `gpg` 命令（本地开发）
 *
 * 要求：模块已应用 'maven-publish' 和 'signing' 插件，并已创建名为 "mavenJava" 的 MavenPublication
 */
fun Project.autoConfigureSigning() {
    // pluginManager.apply("maven-publish")
    pluginManager.apply("signing")

    afterEvaluate {
        val signing = extensions.findByType(SigningExtension::class.java) ?: return@afterEvaluate
        val publishing = extensions.findByType(PublishingExtension::class.java) ?: return@afterEvaluate

        val gpgPrivateKey = providers.environmentVariable("GPG_PRIVATE_KEY").orNull
        val gpgPassphrase = providers.environmentVariable("GPG_PASSPHRASE").orNull

        if (!gpgPrivateKey.isNullOrBlank() && !gpgPassphrase.isNullOrBlank()) {
            signing.useInMemoryPgpKeys(gpgPrivateKey, gpgPassphrase)
            logger.lifecycle("✅ {}: using in-memory GPG key", path)
        } else {
            signing.useGpgCmd()
            logger.lifecycle("🔐 {}: using local gpg command", path)
        }

        val publication = publishing.publications.findByName("mavenJava") as? MavenPublication
        if (publication != null) {
            signing.sign(publication)
            logger.lifecycle("📦 {}: signing publication 'mavenJava'", path)
        } else {
            logger.warn("⚠️ {}: no 'mavenJava' publication found, skip signing", path)
        }
    }
}
