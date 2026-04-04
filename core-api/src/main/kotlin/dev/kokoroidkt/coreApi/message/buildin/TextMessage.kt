// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.message.buildin

import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.message.TextConvertible

class TextMessage(
    val text: String,
) : MessageSegment(),
    TextConvertible {
    override fun toPlainText(): String = text
}
