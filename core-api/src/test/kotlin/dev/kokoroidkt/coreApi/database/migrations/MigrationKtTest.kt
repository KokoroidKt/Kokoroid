// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database.migrations

import dev.kokoroidkt.coreApi.database.DatabaseManager
import dev.kokoroidkt.coreApi.database.allTables
import dev.kokoroidkt.coreApi.database.tables.MigrationTable
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.insert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.exposed.v1.jdbc.transactions.transaction as realTransaction

/**
 * Tests for the trySyncDB function in the MigrationKt class.
 * These tests ensure that database migrations are handled properly.
 */
class MigrationKtTest {
    companion object {
        @AfterAll
        @JvmStatic
        fun `tear down koin`() {
            stopKoin()
        }

        @BeforeAll
        @JvmStatic
        fun `init database connection with h2`() {
            startKoin {
                modules(
                    module {
                        single<DatabaseManager> {
                            object : DatabaseManager {
                                val actualDb =
                                    Database.connect(
                                        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                                        driver = "org.h2.Driver",
                                    )

                                override fun <T> transaction(
                                    db: Database?,
                                    transactionIsolation: Int?,
                                    readOnly: Boolean?,
                                    statement: JdbcTransaction.() -> T,
                                ): T =
                                    realTransaction(
                                        db = actualDb,
                                        transactionIsolation = transactionIsolation,
                                        readOnly = readOnly,
                                        statement = statement,
                                    )

                                override fun init(database: Database) {
                                    // no-op for test
                                }

                                override fun close() {
                                    // no-op for test
                                }
                            }
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `test when no previous version exists then database is initialized`() {
        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(*allTables)
            // Clear the MigrationTable to mimic no previous version entry
            SchemaUtils.create(MigrationTable)
            MigrationTable.deleteAll()
        }

        val result = trySyncDB()

        databaseManager.transaction {
            addLogger(StdOutSqlLogger)
            // Verify that all tables have been created
            allTables.forEach { assertTrue(it.exists()) }
        }

        assertEquals(MigrationResult.CreateNew, result)
    }

    @Test
    fun `test when previous version matches current version then no changes`() {
        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            SchemaUtils.drop(*allTables)
            SchemaUtils.drop(MigrationTable)
        }
        val result = trySyncDB()
        val result2 = trySyncDB()

        assertEquals(MigrationResult.NoChange, result2)
    }

    @Test
    fun `test when previous version does not match current version then migration script is generated`() {
        val databaseManager = getKoin().get<DatabaseManager>()
        val oldHash = "oldVersionHash123"

        databaseManager.transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(*allTables)
            SchemaUtils.create(MigrationTable)
            MigrationTable.deleteAll()
            MigrationTable.insert {
                it[MigrationTable.key] = MIGRATION_VERSION_KEY
                it[MigrationTable.value] = oldHash
            }
        }

        val result = trySyncDB()

        if (result is MigrationResult.NotMatch) {
            assertEquals(oldHash, result.oldHash)
        } else {
            throw AssertionError("Expected result to be MigrationResult.NotMatch, acuall: ${result::class}")
        }
    }
}
