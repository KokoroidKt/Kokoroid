// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.transport.decoder

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.transport.raw.Raw

typealias Decoder = (Raw) -> Event?
