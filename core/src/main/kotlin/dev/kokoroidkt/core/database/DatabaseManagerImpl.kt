// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.core.database

import dev.kokoroidkt.coreApi.database.DatabaseManager
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction as realTransaction

internal object DatabaseManagerImpl : DatabaseManager {
    private lateinit var _database: Database
    val database get() = _database

    override fun init(database: Database) {
        this._database = database
    }

    override fun close() {
    }

    override fun <T> transaction(
        db: Database?,
        transactionIsolation: Int?,
        readOnly: Boolean?,
        statement: JdbcTransaction.() -> T,
    ): T {
        var dbCopy = db
        if (dbCopy == null) {
            dbCopy = _database
        }
        return realTransaction(
            db = dbCopy,
            transactionIsolation = transactionIsolation,
            readOnly = readOnly,
            statement = statement,
        )
    }
}
