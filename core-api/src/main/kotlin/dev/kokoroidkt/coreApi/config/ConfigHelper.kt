package dev.kokoroidkt.coreApi.config

import com.typesafe.config.Config
import java.nio.file.Path

interface ConfigHelper {
    fun decodeHoconFileToConfig(path: Path): Config

    fun <T> decodeHoconFile(path: Path): T

    fun decodeHoconFromString(hocon: String): Config

    fun <T> decodeHoconString(hocon: String): T

    fun <T> encodeHoconToString(data: T): String

    fun <T> encodeHoconToFile(
        data: T,
        path: Path,
    )
}
