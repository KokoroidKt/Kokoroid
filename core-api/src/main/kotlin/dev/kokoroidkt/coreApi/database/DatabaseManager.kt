package dev.kokoroidkt.coreApi.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager

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
