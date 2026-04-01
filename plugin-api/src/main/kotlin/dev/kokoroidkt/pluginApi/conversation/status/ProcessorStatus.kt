// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.conversation.status

import kotlin.reflect.KType

sealed class ProcessorStatus {
    object Processed : ProcessorStatus()

    class Unmatched(
        val expect: Any?,
        val acual: Any?,
    ) : ProcessorStatus()
}
