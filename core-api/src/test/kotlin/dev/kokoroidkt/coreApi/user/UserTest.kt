// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.user

import dev.kokoroidkt.coreApi.database.DatabaseManager
import dev.kokoroidkt.coreApi.database.tables.OperatorTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.jetbrains.exposed.v1.jdbc.transactions.transaction as realTransaction

class UserTest {
    class TestUser(
        adapterId: String,
        override val platformUserId: String,
    ) : User(adapterId)

    companion object {
        private lateinit var testDb: Database

        @AfterAll
        @JvmStatic
        fun `tear down koin`() {
            stopKoin()
        }

        @BeforeAll
        @JvmStatic
        fun `init database connection with h2`() {
            testDb =
                Database.connect(
                    "jdbc:h2:mem:user_test;DB_CLOSE_DELAY=-1",
                    driver = "org.h2.Driver",
                )

            startKoin {
                modules(
                    module {
                        single<DatabaseManager> {
                            object : DatabaseManager {
                                override fun <T> transaction(
                                    db: Database?,
                                    transactionIsolation: Int?,
                                    readOnly: Boolean?,
                                    statement: JdbcTransaction.() -> T,
                                ): T =
                                    realTransaction(
                                        db = testDb,
                                        transactionIsolation = transactionIsolation,
                                        readOnly = readOnly,
                                        statement = statement,
                                    )

                                override fun init(database: Database) {}

                                override fun close() {}
                            }
                        }
                    },
                )
            }
        }
    }

    @BeforeEach
    fun setup() {
        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            SchemaUtils.drop(OperatorTable)
            SchemaUtils.create(OperatorTable)
        }
    }

    @Test
    fun `test isOp returns true when user is an operator`() {
        val adapterId = "test_adapter"
        val platformUserId = "user123"
        val user = TestUser(adapterId, platformUserId)
        val fullUserId = "$platformUserId@$adapterId"

        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            OperatorTable.insert {
                it[userId] = fullUserId
            }
        }

        assertTrue(user.isOp, "User should be an operator")
    }

    @Test
    fun `test isOp returns false when user is not an operator`() {
        val adapterId = "test_adapter"
        val platformUserId = "user456"
        val user = TestUser(adapterId, platformUserId)

        assertFalse(user.isOp, "User should not be an operator")
    }

    @Test
    fun `test isOp is lazy and cached`() {
        val adapterId = "test_adapter"
        val platformUserId = "user789"
        val user = TestUser(adapterId, platformUserId)
        val fullUserId = "$platformUserId@$adapterId"

        // Initially not an op
        assertFalse(user.isOp)

        // Add to operator table
        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            OperatorTable.insert {
                it[userId] = fullUserId
            }
        }

        // Should still be false because it's lazy and already evaluated
        assertFalse(user.isOp, "isOp should be cached and not reflect database changes after first access")
    }
}
