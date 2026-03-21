// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.driverApi.decoder

import dev.kokoroidkt.coreApi.event.Event

interface EventDecoder {
    fun decodeToEvent(): Event?
}
