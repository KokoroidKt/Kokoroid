/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.decoder

import dev.kokoroidkt.coreApi.event.Event

interface EventDecoder {
    fun deocdeToEvent(): Event?
}
