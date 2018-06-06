/*
Copyright 2016 Hermann Krumrey <hermann@krumreyh.com>

This file is part of papio.

papio is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

papio is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with papio.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.namibsun.papio.lib.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteException
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.fail
import kotlin.test.assertTrue

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