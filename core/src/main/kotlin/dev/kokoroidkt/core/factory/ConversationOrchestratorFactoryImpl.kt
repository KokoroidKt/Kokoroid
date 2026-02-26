package dev.kokoroidkt.core.factory

import dev.kokoroidkt.core.conversation.DefaultConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory

class ConversationOrchestratorFactoryImpl : ConversationOrchestratorFactory {
    override fun create(processor: Processor) = DefaultConversationOrchestrator(processor)
}
