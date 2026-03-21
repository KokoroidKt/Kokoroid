// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import dev.kokoroidkt.coreApi.config.ConfigHelper
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import kotlin.io.path.writeText

class ConfigHelperImpl :
    ConfigHelper,
    KoinComponent {
    val config: dev.kokoroidkt.core.config.Config by inject()

    override fun decodeHoconFileToConfig(path: Path): Config {
        val newPath = Path.of("kokoroid/config").resolve(path)
        return ConfigFactory.parseFile(newPath.toFile())
    }

    override fun <T> decodeHoconFile(path: Path): T {
        val newPath = Path.of("kokoroid/config").resolve(path)
        return decodeHoconFile<T>(newPath)
    }

    override fun decodeHoconFromString(hocon: String): Config = ConfigFactory.parseString(hocon)

    override fun <T> decodeHoconString(hocon: String): T = decodeHoconString<T>(hocon)

    override fun <T> encodeHoconToString(data: T): String = encodeHoconToString(data)

    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> encodeHoconToFile(
        data: T,
        path: Path,
    ) {
        val newPath = Path.of("kokoroid/config").resolve(path)
        val hoconStr = encodeHoconToString(data)
        newPath.parent.toFile().mkdir()
        newPath.writeText(hoconStr)
    }
}
