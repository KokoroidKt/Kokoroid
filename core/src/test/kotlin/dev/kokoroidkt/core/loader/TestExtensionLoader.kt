/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.loader

import dev.kokoroidkt.core.config.BasicConfig
import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.core.runtime.KokoroidLauncher
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import kotlin.test.Test

class TestExtensionLoader {
    val kokoroidLauncher = KokoroidLauncher()

    @Test
    fun `test extension loader`() {
        kokoroidLauncher.loadDrivers()
        kokoroidLauncher.loadAdapters()
        kokoroidLauncher.initPlugins()
        kokoroidLauncher.startAdapters()
        kokoroidLauncher.startDrivers()
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun `set test configuration`() {
            try {
                startKoin {
                    modules(allModules)
                }
            } catch (_: KoinApplicationAlreadyStartedException) {
            }
            val cfg =
                BasicConfig.createDefault()
            try {
                // Kotlin 的 lazy 委托会生成一个名为 "basic$delegate" 的字段
                val delegateField = Config::class.java.getDeclaredField("basic\$delegate")
                delegateField.isAccessible = true
                val delegate = delegateField.get(Config())

                // Lazy 实现类有一个 _value 字段
                val valueField = delegate::class.java.getDeclaredField("_value")
                valueField.isAccessible = true
                valueField.set(delegate, cfg)
            } catch (e: NoSuchFieldException) {
                // 如果上面的方法失败，尝试其他可能的字段名
                try {
                    // 有时字段名可能是 "basic$delegate" 或其他变体
                    val fields = Config::class.java.declaredFields
                    println("可用的字段: ${fields.map { it.name }}")

                    // 查找包含 "basic" 的字段
                    val basicField = fields.firstOrNull { it.name.contains("basic") }
                    if (basicField != null) {
                        basicField.isAccessible = true
                        val delegate = basicField.get(Config())

                        // 尝试设置值
                        val valueField =
                            delegate::class.java.declaredFields
                                .firstOrNull { it.name.contains("value", ignoreCase = true) }
                        if (valueField != null) {
                            valueField.isAccessible = true
                            valueField.set(delegate, cfg)
                        }
                    }
                } catch (e2: Exception) {
                    println("警告: 无法通过反射设置配置: ${e2.message}")
                }
            }
        }
    }
}
