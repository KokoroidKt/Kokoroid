/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.conversation.state

enum class ConversationState(
    val state: String,
) {
    READY("READY"),
    OPEN("OPEN"),
    CLOSE("CLOSED"),
}
