// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.adapterApi.exceptions

import dev.kokoroidkt.adapterApi.adapter.AdapterContainer

class AdapterNotFoundException(
    message: String,
    cause: Throwable? = null,
    causeByAdapter: AdapterContainer? = null,
) : AdapterException(message, cause = cause)
