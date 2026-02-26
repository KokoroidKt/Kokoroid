/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.coreApi.exceptions.KokoroidException
import dev.kokoroidkt.pluginApi.plugin.PluginContainer

/**
 * 插件相关异常基类
 * the base exception to kokoroid's plugin
 */
open class PluginException(
    message: String,
    causeByPlugin: PluginContainer? = null,
    cause: Throwable? = null,
) : KokoroidException(message, cause)
