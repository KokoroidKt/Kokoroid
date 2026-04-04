// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.user.internal

import dev.kokoroidkt.coreApi.user.User

class KokoroidSystemUser : User("dev.kokoroidkt.coreApi") {
    override val platformUserId: String
        get() = "###KokoroidSystem###"

    override val userId: String
        get() = "$platformUserId@$adapterId"
}
