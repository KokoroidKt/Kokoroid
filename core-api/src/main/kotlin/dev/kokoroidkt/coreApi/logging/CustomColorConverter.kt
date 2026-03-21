// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase

class CustomColorConverter : ForegroundCompositeConverterBase<ILoggingEvent>() {
    override fun getForegroundColorCode(event: ILoggingEvent): String =
        when (event.level.toInt()) {
            Level.DEBUG_INT -> "92"

            // 浅绿色
            Level.INFO_INT -> "96"

            // 淡蓝色
            Level.WARN_INT -> "33"

            // 橙色
            Level.ERROR_INT -> "91"

            // 红色
            else -> "0" // 重置/默认
        }
}
