// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

interface DatabaseManager {
    fun <T> transaction(
        db: Database? = null,
        transactionIsolation: Int? = null,
        readOnly: Boolean? = null,
        statement: JdbcTransaction.() -> T,
    ): T

    fun init(database: Database)

    fun close()
}
