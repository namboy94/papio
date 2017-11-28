package net.namibsun.papio.lib.db

import net.namibsun.papio.lib.db.models.Category
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager
import kotlin.test.assertEquals
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
}