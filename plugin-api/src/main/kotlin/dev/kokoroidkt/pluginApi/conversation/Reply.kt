// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
