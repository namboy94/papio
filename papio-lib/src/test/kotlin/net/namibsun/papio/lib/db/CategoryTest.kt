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
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.MoneyValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Class that tests the Category class and related DbHandler functions
 */
class CategoryTest {

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
     * Tests creating a category and retrieving it afterwards
     */
    @Test
    fun testCreatingAndGettingCategory() {
        val category = this.handler!!.createCategory("Category")
        assertEquals(category, Category(1, "Category"))
        assertEquals(category, this.handler!!.getCategory("Category"))
        assertEquals(category, this.handler!!.getCategory(1))
    }

    /**
     * Tests creating a category of the same name as an existing category.
     * The second category should not be accepted and instead the original category should be returned
     */
    @Test
    fun testCreatingDuplicateCategory() {
        val original = this.handler!!.createCategory("Category")
        val new = this.handler!!.createCategory("Category")
        assertEquals(original, new)
        assertEquals(original, Category(1, "Category"))
    }

    /**
     * Tests deleting a category from the database
     */
    @Test
    fun testDeletingCategory() {
        val original = this.handler!!.createCategory("Category")
        assertNotNull(this.handler!!.getCategory("Category"))
        original.delete(this.handler!!)
        assertNull(this.handler!!.getWallet("Category"))
    }

    /**
     * Tests retrieving all categories from the database
     */
    @Test
    fun testGettingCategories() {

        val categories = listOf(
                this.handler!!.createCategory("A"),
                this.handler!!.createCategory("B"),
                this.handler!!.createCategory("C")
        )

        val dbCategories = this.handler!!.getCategories()
        assertEquals(3, dbCategories.size)
        for (category in dbCategories) {
            for (original in categories) {
                if (category.name == original.name) {
                    assertEquals(category, original)
                } else {
                    assertNotEquals(category, original)
                }
            }
        }
    }

    /**
     * Tests the toString method of the Category class
     */
    @Test
    fun testStringRepresentation() {
        val category = this.handler!!.createCategory("A")
        assertEquals("Category; ID: 1; Name: A", category.toString())
    }
}