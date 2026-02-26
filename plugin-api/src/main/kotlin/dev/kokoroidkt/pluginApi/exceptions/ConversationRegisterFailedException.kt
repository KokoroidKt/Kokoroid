package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.pluginApi.plugin.PluginContainer

class ConversationRegisterFailedException(
    message: String = "",
    causeByPlugin: PluginContainer?,
    cause: Throwable?,
) : PluginException(message, causeByPlugin, cause)
