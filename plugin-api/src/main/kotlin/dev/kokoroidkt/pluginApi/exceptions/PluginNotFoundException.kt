/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.pluginApi.plugin.PluginContainer

class PluginNotFoundException(
    message: String,
    causeByPlugin: PluginContainer? = null,
    cause: Throwable? = null,
) : PluginException(
        message = message,
        causeByPlugin = causeByPlugin,
        cause = cause,
    )
