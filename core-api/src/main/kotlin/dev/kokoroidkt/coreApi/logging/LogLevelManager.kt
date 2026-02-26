/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

class LogLevelManager {
    companion object {
        fun setLevel(level: Level) {
            val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
            logger.level = level
        }

        fun getLevel(): Level {
            val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
            return rootLogger.level
        }
    }
}
