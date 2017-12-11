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

package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.execute
import net.namibsun.papio.cli.prepareDatabase
import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.Value
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import java.io.PrintStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

/**
 * Class that tests the category root action
 */
class CategoryTester {

    /**
     * A database handler connection
     */
    private var dbHandler: DbHandler? = null

    /**
     * Stream that replaces the stdout stream for printing
     */
    private var out = ByteArrayOutputStream()

    /**
     * Sets up the config file paths for testing and initializes the database handler
     */
    @Before
    fun setUp() {
        Config.papioPath = File("papio-testdir")
        Config.dbPath = File("papio-testdir/data.db")
        Config.cliConfirm = false
        this.dbHandler = prepareDatabase()
        System.setOut(PrintStream(this.out))
    }

    /**
     * Deletes the temporary config files
     */
    @After
    fun deleteConfig() {
        this.dbHandler!!.close()
        Config.papioPath.deleteRecursively()
    }

    /**
     * Tests creating a category
     */
    @Test
    fun testCreatingCategory() {
        assertNull(Category.get(this.dbHandler!!, "Test"))
        execute(arrayOf("category", "create", "Test"))

        val category = Category.get(this.dbHandler!!, "Test")
        assertNotNull(category)

        assertEquals("Category created:\n$category\n", this.out.toString())
    }

    /**
     * Tests creating a category that already exists
     */
    @Test
    fun testCreatingCategoryAgain() {

        assertNull(Category.get(this.dbHandler!!, "Test"))

        execute(arrayOf("category", "create", "Test"))
        val categoryOne = Category.get(this.dbHandler!!, "Test")
        assertNotNull(categoryOne)

        this.out = ByteArrayOutputStream()
        System.setOut(PrintStream(this.out))

        execute(arrayOf("category", "create", "Test"))
        val categoryTwo = Category.get(this.dbHandler!!, "Test")
        assertNotNull(categoryTwo)

        assertEquals(categoryOne, categoryTwo)
        assertEquals("Category already exists:\n$categoryOne\n", this.out.toString())
    }

    /**
     * Tests listing all categories in the database
     */
    @Test
    fun testListingCategories() {
        val one = Category.create(this.dbHandler!!, "One")
        val two = Category.create(this.dbHandler!!, "Two")
        val three = Category.create(this.dbHandler!!, "Three")
        execute(arrayOf("category", "list"))
        assertEquals("$one\n$two\n$three\n", this.out.toString())
    }

    /**
     * Tests displaying a category with both an ID and a name
     */
    @Test
    fun testDisplayingCategory() {

        val categoryData = this.initializeDisplayableCategories()
        val category = categoryData.first
        val transactionOne = categoryData.second
        val transactionTwo = categoryData.third

        execute(arrayOf("category", "display", category.name))
        print("SPLIT")
        execute(arrayOf("category", "display", category.id.toString()))

        for (result in this.out.toString().split("SPLIT")) {
            assertEquals("$category\n\n$transactionTwo\n$transactionOne\n", result)
        }
    }

    /**
     * Tests limiting the amount of transactions to display
     */
    @Test
    fun testDisplayingCategoryWithLimitedTransactions() {
        val categoryData = this.initializeDisplayableCategories()
        val category = categoryData.first
        val transactionOne = categoryData.second
        val transactionTwo = categoryData.third

        execute(arrayOf("category", "display", category.name, "-t", "500"))
        print("SPLIT")
        execute(arrayOf("category", "display", category.name, "-t", "2"))
        print("SPLIT")
        execute(arrayOf("category", "display", category.name, "-t", "1"))
        print("SPLIT")
        execute(arrayOf("category", "display", category.name, "-t", "-1"))
        print("SPLIT")
        execute(arrayOf("category", "display", category.name, "-t", "-1000"))

        val outData = this.out.toString().split("SPLIT")
        assertEquals("$category\n\n$transactionTwo\n$transactionOne\n", outData[0])
        assertEquals("$category\n\n$transactionTwo\n$transactionOne\n", outData[1])
        assertEquals("$category\n\n$transactionTwo\n", outData[2])
        assertEquals("$category\n\n$transactionTwo\n$transactionOne\n", outData[3])
        assertEquals("$category\n\n", outData[4])
    }

    /**
     * Tests displaying a category that does not exist
     */
    @Test
    fun testDisplayingCategoryIfCategoryDoesNotExist() {
        execute(arrayOf("category", "display", "One"))
        assertEquals("Category One does not exist\n", this.out.toString())
    }

    /**
     * Tests deleting a category with both an ID or a name
     */
    @Test
    fun testDeletingCategory() {
        val one = Category.create(this.dbHandler!!, "One")
        val two = Category.create(this.dbHandler!!, "Two")

        assertNotNull(Category.get(this.dbHandler!!, one.id))
        assertNotNull(Category.get(this.dbHandler!!, two.id))

        execute(arrayOf("category", "delete", "One"))
        assertNull(Category.get(this.dbHandler!!, one.id))
        assertNotNull(Category.get(this.dbHandler!!, two.id))

        execute(arrayOf("category", "delete", "2"))
        assertNull(Category.get(this.dbHandler!!, one.id))
        assertNull(Category.get(this.dbHandler!!, two.id))
    }

    /**
     * Tests cancelling a delete operation
     */
    @Test
    fun testCancellingDeletingCategory() {
        val category = Category.create(this.dbHandler!!, "One")
        Config.autoResponse = "n"

        assertNotNull(Category.get(this.dbHandler!!, category.id))
        execute(arrayOf("category", "delete", "One"))
        assertNotNull(Category.get(this.dbHandler!!, category.id))

        Config.autoResponse = "y"
    }

    /**
     * Tests seleting a category that does not exist
     */
    @Test
    fun testDeletingCategoryThatDoesNotExist() {
        execute(arrayOf("category", "delete", "One"))
        assertEquals("Category One does not exist\n", this.out.toString())
    }

    /**
     * Initializes a category with two transactions
     * @return A triple of the category and its transactions
     */
    private fun initializeDisplayableCategories(): Triple<Category, Transaction, Transaction> {
        val category = Category.create(this.dbHandler!!, "One")

        val wallet = Wallet.create(this.dbHandler!!, "Wallet", Value("0", Currency.EUR))
        val partner = TransactionPartner.create(this.dbHandler!!, "Partner")
        val transactionOne = Transaction.create(
                this.dbHandler!!, wallet, category, partner,
                "Desc1", Value("1", Currency.EUR), IsoDate("2000-01-01")
        )
        val transactionTwo = Transaction.create(
                this.dbHandler!!, wallet, category, partner,
                "Desc2", Value("2", Currency.USD), IsoDate("2017-01-01")
        )
        return Triple(category, transactionOne, transactionTwo)
    }
}