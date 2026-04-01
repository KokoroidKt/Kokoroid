package dev.kokoroidkt.pluginApi.conversation.command

import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.message.TextConvertible

sealed class CommandArg {
    data class Segment(
        val value: MessageSegment,
    ) : CommandArg()

    data class TextArg(
        val value: String,
    ) : CommandArg()
}
