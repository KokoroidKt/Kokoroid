/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigOriginFactory
import com.typesafe.config.ConfigValueFactory
import dev.kokoroidkt.core.constants.DefaultPaths
import dev.kokoroidkt.coreApi.config.decodeHoconFromHoconConfig
import dev.kokoroidkt.coreApi.config.encodeHocon
import dev.kokoroidkt.coreApi.config.render
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import kotlinx.serialization.ExperimentalSerializationApi
import logger.getLogger
import kotlin.io.path.exists

class Config {
    @Volatile
    private var overrideBasic: BasicConfig? = null
    val logger = getLogger("Config")

    val basic: BasicConfig by lazy {
        overrideBasic ?: if (!DefaultPaths.BASIC_CONFIG_PATH.exists()) {
            createDefaultConfig()
        } else {
            loadExistingConfig()
        }
    }

    private fun createDefaultConfig(): BasicConfig {
        DefaultPaths.CONFIG_DIRECTORY.toFile().mkdirs()
        val defaultBasicConfig = BasicConfig.createDefault()
        val config =
            ConfigFactory
                .empty()
                .withValue(
                    "use",
                    ConfigValueFactory
                        .fromAnyRef("default")
                        .withOrigin(ConfigOriginFactory.newSimple().withComments(listOf("使用的配置块"))),
                ).withValue("default", encodeHocon<BasicConfig>(defaultBasicConfig).root())
        DefaultPaths.BASIC_CONFIG_PATH.toFile().writeText(config.render())
        return defaultBasicConfig
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadExistingConfig(): BasicConfig {
        val rootConfig = ConfigFactory.parseFile(DefaultPaths.BASIC_CONFIG_PATH.toFile())
        val chosenProfile = runCatching { rootConfig.getString("use") }.getOrDefault("default")

        val profileConfig =
            runCatching { rootConfig.getConfig(chosenProfile) }.getOrElse {
                throw CriticalException(
                    "Config `$chosenProfile` not exist, " +
                        "please check config.conf to make sure the option of `$chosenProfile` is exist",
                )
            }
        return decodeHoconFromHoconConfig<BasicConfig>(profileConfig)
    }
}
