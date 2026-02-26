/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.utils

import java.util.jar.Manifest

/**
 * Kokoroid 版本信息
 */
object KokoroidVersion {
    /**
     * 完整版本号 (例如: 1.0.0)
     */
    val version: String by lazy {
        loadManifestAttribute("Implementation-Version") ?: "Unknown"
    }

    /**
     * Git 提交哈希值 (例如: a1b2c3d)
     */
    val gitHash: String by lazy {
        loadManifestAttribute("Git-Hash") ?: "Unknown"
    }

    /**
     * 完整版本字符串 (例如: 1.0.0.a1b2c3d)
     */
    val fullVersion: String by lazy {
        "$version.$gitHash"
    }

    /**
     * 从 Manifest 文件中加载指定属性的值
     */
    private fun loadManifestAttribute(attributeName: String): String? =
        try {
            // 获取当前线程的上下文 ClassLoader
            val classLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()

            // 查找包含我们特定属性的 Manifest
            classLoader
                .getResources("META-INF/MANIFEST.MF")
                .asSequence()
                .firstOrNull { url ->
                    url.openStream().use { stream ->
                        val manifest = Manifest(stream)
                        "Kokoroid Core" == manifest.mainAttributes.getValue("Implementation-Title")
                    }
                }?.openStream()
                ?.use { stream ->
                    Manifest(stream).mainAttributes.getValue(attributeName)
                }
        } catch (e: Exception) {
            // e.printStackTrace()
            null
        }
}
