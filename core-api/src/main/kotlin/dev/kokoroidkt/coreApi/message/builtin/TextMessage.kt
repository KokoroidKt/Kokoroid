package dev.kokoroidkt.coreApi.message.builtin

import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.message.TextConvertible

class TextMessage(
    val text: String,
) : MessageSegment(),
    TextConvertible {
    override fun toPlainText(): String = text
}
