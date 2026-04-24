// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.driverApi.transport

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.transport.decoder.Decoder
import dev.kokoroidkt.transport.raw.Raw
import org.koin.java.KoinJavaComponent.getKoin

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

    companion object {
        /**
         * 向主事件循环提交一个事件
         *
         * @param event
         */
        suspend fun emit(event: Event?) {
            getKoin().get<GlobalLoopEmitter>().emit(event)
        }
    }
}
