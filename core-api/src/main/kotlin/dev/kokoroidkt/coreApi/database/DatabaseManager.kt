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
