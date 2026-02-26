/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.coreApi.exceptions.KokoroidException

/**
 * 对话关闭异常
 * 当此异常被抛出时，当前对话关闭
 *
 * @constructor Create empty Conversation close exception
 */
class ConversationCloseException(
    message: String,
    cause: Throwable? = null,
) : KokoroidException(message, cause)
