/*
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

import net.namibsun.papio.lib.db.models.TransactionPartner
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Class that tests the TransactionPartner class and related DbHandler functions
 */
class TransactionPartnerTest {

    /**
     * The DbHandler used in this test class
     */
    private var handler: DbHandler? = null

    /**
     * Sets up an SQLITE database connection and DbHandler before each test
     */
    @Before
    fun setUp() {
        val connection = DriverManager.getConnection("jdbc:sqlite:test.db")
        this.handler = DbHandler(connection)
    }

    /**
     * Closes the SQLITE database connection and deletes the database file
     */
    @After
    fun tearDown() {
        this.handler!!.close()
        File("test.db").delete()
    }

    /**
     * Tests creating a transaction partner and retrieving it afterwards
     */
    @Test
    fun testCreatingAndGettingTransactionPartner() {
        val partner = this.handler!!.createTransactionPartner("Partner")
        assertEquals(partner, TransactionPartner(1, "Partner"))
        assertEquals(partner, this.handler!!.getTransactionPartner("Partner"))
        assertEquals(partner, this.handler!!.getTransactionPartner(1))
    }
    /**
     * Tests creating a category of the same name as an existing category.
     * The second category should not be accepted and instead the original category should be returned
     */
    @Test
    fun testCreatingDuplicateTransactionPartner() {
        val original = this.handler!!.createTransactionPartner("Partner")
        val new = this.handler!!.createTransactionPartner("Partner")
        assertEquals(original, new)
        assertEquals(original, TransactionPartner(1, "Partner"))
    }

    /**
     * Tests deleting a transaction partner from the database
     */
    @Test
    fun testDeletingTransactionPartner() {
        val original = this.handler!!.createTransactionPartner("Partner")
        assertNotNull(this.handler!!.getTransactionPartner("Partner"))
        original.delete(this.handler!!)
        assertNull(this.handler!!.getTransactionPartner("Partner"))
    }
}