package net.namibsun.papio.lib.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteException
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

/**
 * Class that tests the creation of the database tables and their content
 */
class DbCreationTest {

    private var connection: Connection? = null

    /**
     * Sets up an SQLITE database connection before each test
     */
    @Before
    fun setUp() {
        this.connection = DriverManager.getConnection("jdbc:sqlite:test.db")
    }

    /**
     * Closes the SQLITE database connection and deletes the database file
     */
    @After
    fun tearDown() {
        this.connection!!.close()
        File("test.db").delete()
    }

    /**
     * Tests if the DbHandler constructor correctly generates the database tables
     */
    @Test
    fun testInitializing() {

        val statement = this.connection!!.createStatement()
        val tables = listOf("wallets", "transactions", "categories", "transaction_partners")
        for (table in tables) {
            try {
                statement.execute("SELECT * FROM $table")
                fail()
            } catch (e: SQLiteException) {
            }
        }

        DbHandler(this.connection!!)

        for (table in tables) {
            assertTrue(statement.execute("SELECT * FROM $table"))
        }

        try {
            statement.execute("SELECT * FROM not_existing_table")
            fail()
        } catch (e: SQLiteException) {
        }
        statement.close()
    }
}