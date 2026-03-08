package dev.kokoroidkt.pluginApi.conversation.status

import kotlin.reflect.KType

sealed class ProcessorStatus {
    object Processed : ProcessorStatus()

    class Unmatched(
        val expect: KType,
        val acual: Any?,
    ) : ProcessorStatus()
}
