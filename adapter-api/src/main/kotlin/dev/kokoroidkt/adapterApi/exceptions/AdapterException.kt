// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.adapterApi.exceptions

import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.coreApi.exceptions.KokoroidException

abstract class AdapterException(
    message: String,
    cause: Throwable? = null,
    causeByAdapter: AdapterContainer? = null,
) : KokoroidException(message, cause = cause)
