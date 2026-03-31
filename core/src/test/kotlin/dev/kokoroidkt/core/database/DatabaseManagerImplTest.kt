package dev.kokoroidkt.core.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for DatabaseManagerImpl class.
 * This class tests the functionality of the getDatabase() method
 * and the transaction system of the DatabaseManagerImpl object.
 */
class DatabaseManagerImplTest {
    private lateinit var mockDatabase: Database
    private lateinit var mockDatabase2: Database

    @BeforeEach
    fun setUp() {
        // Mock a simple database connection; replace with real mock logic if needed.
        mockDatabase = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        mockDatabase2 = Database.connect("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

        DatabaseManagerImpl.init(mockDatabase)
    }

    @Test
    fun `test if init sets the database successfully`() {
        assertEquals(mockDatabase, DatabaseManagerImpl.database, "Database should be initialized correctly.")
    }

    @Test
    fun `test multiple transactions run sequentially`() {
        val usedDatabases = mutableListOf<Database>()

        repeat(5) {
            DatabaseManagerImpl.transaction(null, null, null) {
                usedDatabases.add(db)
            }
        }

        assertEquals(5, usedDatabases.size, "All transactions should have executed correctly.")
        assertTrue(usedDatabases.all { it == mockDatabase }, "All transactions should use the default database.")
    }
}
