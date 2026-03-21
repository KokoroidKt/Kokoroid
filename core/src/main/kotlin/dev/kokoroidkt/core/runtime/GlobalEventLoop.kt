// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.runtime

import dev.kokoroid.transport.decoder.Decoder
import dev.kokoroid.transport.raw.Raw
import dev.kokoroidkt.adapterApi.transport.EventEmitter
import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.exceptions.EventBufferIsFullException
import dev.kokoroidkt.core.exceptions.state.ErrorSessionStateException
import dev.kokoroidkt.core.logger.getLogger
import dev.kokoroidkt.core.plugin.PluginManager
import dev.kokoroidkt.core.runtime.crash.CrashRegistry
import dev.kokoroidkt.core.runtime.state.InternalState
import dev.kokoroidkt.core.runtime.state.RuntimeState
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.session.SessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.cancellation.CancellationException

class GlobalEventLoop :
    EventEmitter,
    KoinComponent {
    private val config: Config by inject()
    private val eventChannel = Channel<Event>(config.basic.performance.global.bufferSize)
    private val decoderDispatcher =
        Dispatchers.Default.limitedParallelism(config.basic.performance.eventDecoderMaxParallelism)
    private val crashRegistry: CrashRegistry by inject()

    private val logger = getLogger("MainLoop")
    private val runtimeState by inject<RuntimeState>()
    private val pluginManager: PluginManager by inject()

    suspend fun start() {
        logger.info { "Kokoroid Start Successfully!!! \n" }
        while (runtimeState.state is InternalState.Running) {
            val event = eventChannel.receive()
            logger.debug { "receive ${event.eventId}(${event::class.qualifiedName})" }
            withContext(Dispatchers.Default) {
                try {
                    pluginManager.pluginList.forEach { plugin ->
                        logger.debug { "Plugin ${plugin.pluginId} processing" }
                        plugin.orchestrators.forEach { orchestrator ->
                            logger.debug { "orchestrator with processor ${orchestrator.getProcessorQualifiedName()} processing" }
                            val promise = orchestrator.callSessionToProcessOrCreate(event, event.bot)
                            promise.deferred.await()
                            if (promise.session.state !is SessionState.Finished) {
                                throw ErrorSessionStateException(
                                    SessionState.Finished(Reply.NoReply),
                                    promise.session.state,
                                )
                            }
                            when (val reply = (promise.session.state as SessionState.Finished).reply) {
                                is Reply.NoReply -> {}

                                is Reply.Unprocessed -> {}

                                is Reply.MessageChainReply -> {
                                    event.bot.replyMessage(event, reply.chain)
                                }

                                is Reply.BackgroundTaskReply -> {
                                    launch { reply.task() }
                                }
                            }
                            if (event.propagationStopped) {
                                logger.debug {
                                    "Event ${event.eventId} stopped propagation " +
                                        "when $plugin (${orchestrator.getProcessorQualifiedName()}) is processing"
                                }
                                return@withContext
                            }
                            logger.debug { "orchestrator with processor ${orchestrator.getProcessorQualifiedName()} finished" }
                        }
                        logger.debug { "Plugin${plugin.pluginId} processed done" }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: CriticalException) {
                    logger.error { "CRITICAL Error occurred while processing event: ${event.javaClass.name}" }
                    crashRegistry.recordAndRequestStop(e, event)
                } catch (t: Throwable) {
                    logger.error(t) { "Error occurred while processing event: ${event.javaClass.name}" }
                }
            }
        }
    }

    override suspend fun emit(
        raw: Raw,
        decoder: Decoder,
    ): Unit =
        withContext(decoderDispatcher) {
            decoder.invoke(raw)?.let { emit(it) }
        }

    override suspend fun emit(event: Event?) {
        if (event == null) return
        var tryTime = 0
        while (tryTime < config.basic.performance.global.maxRetryTimes) {
            delay(config.basic.performance.global.retryDelay)
            tryTime++
            if (trySend(event)) return
        }
        throw EventBufferIsFullException("Global", tryTime)
    }

    private fun trySend(event: Event): Boolean {
        val result = eventChannel.trySend(event)
        if (result.isClosed) {
            throw CriticalException("Global Event channel is closed for send, but someone is still trying to send: $event")
        }
        if (result.isSuccess) {
            return true
        } else if (
            !config.basic.performance.global.retryWhenBufferIsFull
        ) {
            throw EventBufferIsFullException("Global", null)
        }
        return false
    }
}
