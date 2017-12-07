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

import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.TransactionPartner
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager
import kotlin.test.*

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
        val partner = TransactionPartner.create(this.handler!!, "Partner")
        assertEquals(partner, TransactionPartner(1, "Partner"))
        assertEquals(partner, TransactionPartner.get(this.handler!!, "Partner"))
        assertEquals(partner, TransactionPartner.get(this.handler!!, 1))
    }
    /**
     * Tests creating a category of the same name as an existing category.
     * The second category should not be accepted and instead the original category should be returned
     */
    @Test
    fun testCreatingDuplicateTransactionPartner() {
        val original = TransactionPartner.create(this.handler!!, "Partner")
        val new = TransactionPartner.create(this.handler!!, "Partner")
        assertEquals(original, new)
        assertEquals(original, TransactionPartner(1, "Partner"))
    }

    /**
     * Tests deleting a transaction partner from the database
     */
    @Test
    fun testDeletingTransactionPartner() {
        val original = TransactionPartner.create(this.handler!!, "Partner")
        assertNotNull(TransactionPartner.get(this.handler!!, "Partner"))
        original.delete(this.handler!!)
        assertNull(TransactionPartner.get(this.handler!!, "Partner"))
    }

    /**
     * Tests retrieving all transaction partners from the database
     */
    @Test
    fun testGettingTransactionPartners() {

        val originals = listOf(
                TransactionPartner.create(this.handler!!, "A"),
                TransactionPartner.create(this.handler!!, "B"),
                TransactionPartner.create(this.handler!!, "C")
        )

        val dbPartners = TransactionPartner.getAll(this.handler!!)
        assertEquals(3, dbPartners.size)
        for (partner in dbPartners) {
            for (original in originals) {
                if (partner.name == original.name) {
                    assertEquals(partner, original)
                } else {
                    assertNotEquals(partner, original)
                }
            }
        }
    }

    /**
     * Tests the toString method of the TransactionPartner class
     */
    @Test
    fun testStringRepresentation() {
        val partner = TransactionPartner.create(this.handler!!, "A")
        assertEquals("TRANSACTION_PARTNERS; ID: 1; Name: A;", partner.toString())
    }

    /**
     * Tests the equals() Method
     */
    @Test
    fun testEquality() {
        val partner = TransactionPartner(1, "A")
        assertEquals(partner, TransactionPartner(1, "A"))
        assertNotEquals(partner, TransactionPartner(2, "A"))
        assertNotEquals(partner, TransactionPartner(1, "B"))
        @Suppress("ReplaceCallWithComparison")
        (assertFalse(partner.equals(Category(1, "A"))))
    }

    /**
     * Tests fetching all TransactionPartners from the database
     */
    @Test
    fun testGettingAllCategories() {
        assertEquals(0, TransactionPartner.getAll(this.handler!!).size)
        val one = TransactionPartner.create(this.handler!!, "A")
        val two = TransactionPartner.create(this.handler!!, "B")
        val partners = TransactionPartner.getAll(this.handler!!).sortedBy { it.id }
        assertEquals(2, partners.size)
        assertEquals(one, partners[0])
        assertEquals(two, partners[1])
    }
}