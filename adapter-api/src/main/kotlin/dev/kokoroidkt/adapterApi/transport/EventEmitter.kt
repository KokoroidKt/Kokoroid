package dev.kokoroidkt.adapterApi.transport

import dev.kokoroid.transport.decoder.Decoder
import dev.kokoroid.transport.raw.Raw
import dev.kokoroidkt.coreApi.event.Event

interface EventEmitter {
    /**
     * 通过原始数据和decoder推送事件
     * [Decoder]会解析raw，最终提交一个事件
     * @param raw 原始数据
     * @param decoder 解码器
     */
    suspend fun emit(
        raw: Raw,
        decoder: Decoder,
    )

    /**
     * 直接提交一个事件
     *
     * @param event 要提交的事件
     */
    suspend fun emit(event: Event?)
}
