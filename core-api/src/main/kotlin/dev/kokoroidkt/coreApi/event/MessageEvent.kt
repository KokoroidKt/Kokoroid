// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.event

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.UserGroup
import java.time.Instant

/**
 * 带消息的事件
 * 此类事件携带MessageChain，可以解析出消息内容
 *
 * @property messageChain 消息链
 * @constructor
 *

 */
interface MessageEvent {
    val messageChain: MessageChain
}
