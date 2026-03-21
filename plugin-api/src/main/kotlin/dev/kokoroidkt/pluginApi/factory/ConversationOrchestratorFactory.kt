// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.factory

import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor

interface ConversationOrchestratorFactory {
    fun create(processor: Processor): ConversationOrchestrator
}
