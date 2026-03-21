// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.conversation.state

enum class ConversationState(
    val state: String,
) {
    READY("READY"),
    OPEN("OPEN"),
    CLOSE("CLOSED"),
}
