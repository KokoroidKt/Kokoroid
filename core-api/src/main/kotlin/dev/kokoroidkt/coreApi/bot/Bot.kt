/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.bot

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import kotlinx.serialization.json.JsonElement

/**
 * Bot接口
 * Bot实现对某个平台API的操作，例如
 *  - 发送消息
 *  - 同意加群请求
 * Bot Interface
 */
interface Bot {
    fun callApi(
        apiEndpoint: String,
        data: JsonElement,
    )

    fun replyMessage(
        event: Event,
        message: MessageChain,
    )

    val botId: String
}
