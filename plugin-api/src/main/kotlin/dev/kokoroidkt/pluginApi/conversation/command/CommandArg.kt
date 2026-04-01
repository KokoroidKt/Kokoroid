// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.pluginApi.conversation.command

import dev.kokoroidkt.coreApi.message.MessageSegment

sealed class CommandArg {
    data class Segment(
        val value: MessageSegment,
    ) : CommandArg()

    data class TextArg(
        val value: String,
    ) : CommandArg()
}
