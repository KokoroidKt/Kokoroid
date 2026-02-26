/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroid.transport.decoder

import dev.kokoroid.transport.raw.Raw
import dev.kokoroidkt.coreApi.event.Event

typealias Decoder = (Raw) -> Event?
