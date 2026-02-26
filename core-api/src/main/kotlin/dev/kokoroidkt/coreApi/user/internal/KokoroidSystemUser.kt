/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.user.internal

import dev.kokoroidkt.coreApi.user.User

class KokoroidSystemUser : User() {
    override val userId: String
        get() = "###KokoroidSystem###@dev.kokoroidkt.coreApi"
}
