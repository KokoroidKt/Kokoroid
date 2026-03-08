/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.conversation

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.pluginApi.task.BackgroundTask

sealed class Reply {
    data class MessageChainReply(
        val chain: MessageChain,
    ) : Reply()

    object NoReply : Reply()

    object Unprocessed : Reply()

    data class BackgroundTaskReply(
        val task: BackgroundTask,
    ) : Reply()
}
