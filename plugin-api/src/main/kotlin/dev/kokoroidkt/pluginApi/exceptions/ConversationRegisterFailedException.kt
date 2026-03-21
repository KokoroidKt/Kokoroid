// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.pluginApi.plugin.PluginContainer

class ConversationRegisterFailedException(
    message: String = "",
    causeByPlugin: PluginContainer?,
    cause: Throwable?,
) : PluginException(message, causeByPlugin, cause)
