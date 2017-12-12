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
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFalse
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
        val category = Category.create(this.handler!!, "Category")
        assertEquals(category, Category(1, "Category"))
        assertEquals(category, Category.get(this.handler!!, "Category"))
        Category.get(this.handler!!, "1")
        assertEquals(category, Category.get(this.handler!!, 1))
    }

    /**
     * Tests creating a category of the same name as an existing category.
     * The second category should not be accepted and instead the original category should be returned
     */
    @Test
    fun testCreatingDuplicateCategory() {
        val original = Category.create(this.handler!!, "Category")
        val new = Category.create(this.handler!!, "Category")
        assertEquals(original, new)
        assertEquals(original, Category(1, "Category"))
    }

    /**
     * Tests deleting a category from the database
     */
    @Test
    fun testDeletingCategory() {
        val original = Category.create(this.handler!!, "Category")
        assertNotNull(Category.get(this.handler!!, "Category"))
        original.delete(this.handler!!)
        assertNull(Category.get(this.handler!!, "Category"))
    }

    /**
     * Tests retrieving all categories from the database
     */
    @Test
    fun testGettingCategories() {

        val categories = listOf(
                Category.create(this.handler!!, "A"),
                Category.create(this.handler!!, "B"),
                Category.create(this.handler!!, "C")
        )

        val dbCategories = Category.getAll(this.handler!!)
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
        val category = Category.create(this.handler!!, "A")
        assertEquals("CATEGORIES; ID: 1; Name: A;", category.toString())
    }

    /**
     * Tests the equals() Method
     */
    @Test
    fun testEquality() {
        val category = Category(1, "A")
        assertEquals(category, Category(1, "A"))
        assertNotEquals(category, Category(2, "A"))
        assertNotEquals(category, Category(1, "B"))
        @Suppress("ReplaceCallWithComparison")
        assertFalse(category.equals(TransactionPartner(1, "A")))
    }

    /**
     * Tests fetching all Categories from the database
     */
    @Test
    fun testGettingAllCategories() {
        assertEquals(0, Category.getAll(this.handler!!).size)
        val one = Category.create(this.handler!!, "A")
        val two = Category.create(this.handler!!, "B")
        val categories = Category.getAll(this.handler!!).sortedBy { it.id }
        assertEquals(2, categories.size)
        assertEquals(one, categories[0])
        assertEquals(two, categories[1])
    }
}