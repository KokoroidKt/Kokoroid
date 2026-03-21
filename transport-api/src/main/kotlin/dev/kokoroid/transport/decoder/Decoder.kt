// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroid.transport.decoder

import dev.kokoroid.transport.raw.Raw
import dev.kokoroidkt.coreApi.event.Event

typealias Decoder = (Raw) -> Event?
