// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.factory

import dev.kokoroidkt.core.conversation.DefaultConversationOrchestrator
import dev.kokoroidkt.pluginApi.Processable
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory

class ConversationOrchestratorFactoryImpl : ConversationOrchestratorFactory {
    override fun create(processor: Processable) = DefaultConversationOrchestrator(processor)
}
