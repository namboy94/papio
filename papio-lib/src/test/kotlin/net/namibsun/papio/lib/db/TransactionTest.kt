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

import net.namibsun.papio.lib.date.IsoDate
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
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Class that tests Transaction-related actions
 */
class TransactionTest {

    /**
     * The DbHandler used in this test class
     */
    private var handler: DbHandler? = null

    /**
     * The wallet used for testing
     */
    private var wallet: Wallet? = null

    /**
     * The category used for testing
     */
    private var category: Category? = null

    /**
     * The transaction partner used for testing
     */
    private var partner: TransactionPartner? = null

    /**
     * Sets up an SQLITE database connection and DbHandler before each test.
     * Also generates wallet, category and partner
     */
    @Before
    fun setUp() {
        val connection = DriverManager.getConnection("jdbc:sqlite:test.db")
        this.handler = DbHandler(connection)
        this.wallet = Wallet.create(this.handler!!, "Wallet", Value("100", Currency.EUR))
        this.category = Category.create(this.handler!!, "Category")
        this.partner = TransactionPartner.create(this.handler!!, "Partner")
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
     * Tests creating a transaction
     */
    @Test
    fun testCreatingTransaction() {
        val transaction = Transaction.create(
                this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "Description", Value("100", Currency.EUR), IsoDate("1970-01-01")
        )
        assertEquals(
                transaction,
                Transaction(
                        1, this.wallet!!, this.category!!, this.partner!!,
                        "Description", Value("100", Currency.EUR), IsoDate("1970-01-01")
                )
        )
    }

    /**
     * Tests creating a transaction with a currency that differs from the wallet's currency
     * This should lead to the transaction's value being converted to the correct currency before storing
     */
    @Test
    fun testCreatingTransactionWithWrongCurrency() {
        val value = Value("100", Currency.USD)
        val transaction = Transaction.create(
                this.handler!!, this.wallet!!, this.category!!, this.partner!!, "Description", value
        )
        assertEquals(transaction.getAmount().currency, Currency.EUR)
        assertNotEquals(transaction.getAmount(), value)
    }

    /**
     * Tests creating and subsequently deleting a transaction
     */
    @Test
    fun testDeletingTransaction() {
        val transaction = Transaction.create(
                this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "Description", Value("100", Currency.EUR), IsoDate("1970-01-01")
        )
        assertEquals(1, this.wallet!!.getTransactions(this.handler!!).size)
        transaction.delete(this.handler!!)
        assertEquals(0, this.wallet!!.getTransactions(this.handler!!).size)
    }

    /**
     * Tests creating two identical transactions. This should be possible, with both transactions being assigned
     * different IDs.
     */
    @Test
    fun testCreatingIdenticalTransactions() {
        val transactionOne = Transaction.create(
                this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "Description", Value("100", Currency.EUR), IsoDate("1970-01-01")
        )
        val transactionTwo = Transaction.create(
                this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "Description", Value("100", Currency.EUR), IsoDate("1970-01-01")
        )
        assertNotEquals(transactionOne, transactionTwo)
        assertNotEquals(transactionOne.id, transactionTwo.id)
        assertEquals(transactionOne.wallet, transactionTwo.wallet)
        assertEquals(transactionOne.category, transactionTwo.category)
        assertEquals(transactionOne.partner, transactionTwo.partner)
        assertEquals(transactionOne.description, transactionTwo.description)
        assertEquals(transactionOne.date, transactionTwo.date)
        assertEquals(transactionOne.getAmount(), transactionTwo.getAmount())
    }

    /**
     * Tests retrieving all transactions for a wallet
     */
    @Test
    fun testGettingTransactionsForWallet() {
        val amount = Value("1", Currency.EUR)
        val newWallet = Wallet.create(this.handler!!, "NewWallet", Value("0", Currency.EUR))
        for (i in 1..5) {
            Transaction.create(this.handler!!, newWallet, this.category!!, this.partner!!, "$i", amount)
        }
        Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!, "A", amount)

        assertEquals(5, newWallet.getTransactions(this.handler!!).size)
        assertEquals(1, this.wallet!!.getTransactions(this.handler!!).size)

        val indices = mutableListOf(1, 2, 3, 4, 5)
        for (transaction in newWallet.getTransactions(this.handler!!)) {
            assertTrue(transaction.id in indices)
            indices.remove(transaction.id)
        }
        assertEquals(0, indices.size)
    }

    /**
     * Tests retrieving all transactions for a category
     */
    @Test
    fun testGettingTransactionsForCategory() {
        val amount = Value("1", Currency.EUR)
        val newCategory = Category.create(this.handler!!, "NewCategory")
        for (i in 1..5) {
            Transaction.create(this.handler!!, this.wallet!!, newCategory, this.partner!!, "$i", amount)
        }
        Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!, "A", amount)

        assertEquals(5, newCategory.getTransactions(this.handler!!).size)
        assertEquals(1, this.category!!.getTransactions(this.handler!!).size)

        val indices = mutableListOf(1, 2, 3, 4, 5)
        for (transaction in newCategory.getTransactions(this.handler!!)) {
            assertTrue(transaction.id in indices)
            indices.remove(transaction.id)
        }
        assertEquals(0, indices.size)
    }

    /**
     * Tests retrieving all transactions for a transaction partner
     */
    @Test
    fun testGettingTransactionsForTransactionPartner() {
        val amount = Value("1", Currency.EUR)
        val newPartner = TransactionPartner.create(this.handler!!, "NewPartner")
        for (i in 1..5) {
            Transaction.create(this.handler!!, this.wallet!!, this.category!!, newPartner, "$i", amount)
        }
        Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!, "A", amount)

        assertEquals(5, newPartner.getTransactions(this.handler!!).size)
        assertEquals(1, this.partner!!.getTransactions(this.handler!!).size)

        val indices = mutableListOf(1, 2, 3, 4, 5)
        for (transaction in newPartner.getTransactions(this.handler!!)) {
            assertTrue(transaction.id in indices)
            indices.remove(transaction.id)
        }
        assertEquals(0, indices.size)
    }

    /**
     * Tests if the date check works correctly
     */
    @Test
    fun testTransactionDates() {

        val valid = listOf(
                "2017-01-01",
                "1970-01-01",
                "1000-10-10",
                "2017-01-31",
                "2017-02-28",
                "2016-02-29",
                "2017-03-31",
                "2017-04-30",
                "2017-05-31",
                "2017-06-30",
                "2017-07-31",
                "2017-08-31",
                "2017-09-30",
                "2017-10-31",
                "2017-11-30",
                "2017-12-31"
        )

        val invalid = listOf(
                "01.01.1970",
                "2017-01-0A",
                "2017-0A-01",
                "201A-01-01",
                "ABC",
                "2017:01:01",
                "2017/01/01",
                "01-01-2017",
                "2017-13-01",
                "2017-13-01",
                "2017-01-32",
                "2017-02-29",
                "2016-02-30",
                "2017-03-32",
                "2017-04-31",
                "2017-05-32",
                "2017-06-31",
                "2017-07-32",
                "2017-08-32",
                "2017-09-31",
                "2017-10-32",
                "2017-11-31",
                "2017-12-32",
                "2017-01-1",
                "2017-1-01",
                "1-01-01"
        )

        for (date in valid) {
            try {
                Transaction.create(
                        this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate(date)
                )
            } catch (e: IllegalArgumentException) {
                fail()
            }
        }

        for (date in invalid) {
            try {
                Transaction.create(
                        this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate(date)
                )
                fail()
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    /**
     * Tests creating a transaction with an invalid date
     */
    @Test
    fun testCreatingTransactionWithInvalidDate() {
        try {
            Transaction(
                    1, this.wallet!!, this.category!!, this.partner!!,
                    "A", Value("1", Currency.EUR), IsoDate("01.01.1970")
            )
            fail()
        } catch (e: IllegalArgumentException) {
        }
    }

    /**
     * Tests the toString method of the Transaction class
     */
    @Test
    fun testStringRepresentation() {
        val transaction = Transaction.create(
                this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "Desc", Value("500", Currency.EUR), IsoDate("2017-01-01")
        )
        assertEquals(
                "TRANSACTIONS; ID: 1; Wallet: Wallet; Category: Category; Transaction Partner: Partner; " +
                "Description: Desc; Amount: EUR 500.00; Date: 2017-01-01",
                transaction.toString()
        )
    }

    /**
     * Tests if the equals() Method works as intended
     */
    @Test
    fun testEquality() {
        val altWallet = Wallet.create(this.handler!!, "AltWallet", Value("0", Currency.EUR))
        val altCategory = Category.create(this.handler!!, "AltCategory")
        val altPartner = TransactionPartner.create(this.handler!!, "AltPartner")

        val transaction = Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "A", Value("1", Currency.EUR), IsoDate("2017-01-01"))
        assertEquals(transaction.id, 1)
        val ident = Transaction(1, this.wallet!!, this.category!!, this.partner!!,
                "A", Value("1", Currency.EUR), IsoDate("2017-01-01"))

        assertEquals(transaction, ident)

        for (alternative in listOf(
                Transaction(2, this.wallet!!, this.category!!, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate("2017-01-01")),
                Transaction(1, altWallet, this.category!!, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate("2017-01-01")),
                Transaction(1, this.wallet!!, altCategory, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate("2017-01-01")),
                Transaction(1, this.wallet!!, this.category!!, altPartner,
                        "A", Value("1", Currency.EUR), IsoDate("2017-01-01")),
                Transaction(1, this.wallet!!, this.category!!, this.partner!!,
                        "B", Value("1", Currency.EUR), IsoDate("2017-01-01")),
                Transaction(1, this.wallet!!, this.category!!, this.partner!!,
                        "A", Value("1", Currency.USD), IsoDate("2017-01-01")),
                Transaction(1, this.wallet!!, this.category!!, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate("2017-01-02")),
                Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                        "A", Value("1", Currency.EUR), IsoDate("2017-01-01"))
        )) {
            assertNotEquals(transaction, alternative)
        }

        @Suppress("ReplaceCallWithComparison")
        assertFalse(transaction.equals("Test"))
    }

    /**
     * Tests fetching all TransactionPartners from the database
     */
    @Test
    fun testGettingAllCategories() {
        assertEquals(0, Transaction.getAll(this.handler!!).size)
        val one = Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "A", Value("1.0", Currency.EUR))
        val two = Transaction.create(this.handler!!, this.wallet!!, this.category!!, this.partner!!,
                "B", Value("1.0", Currency.EUR))
        val transactions = Transaction.getAll(this.handler!!).sortedBy { it.id }
        assertEquals(2, transactions.size)
        assertEquals(one, transactions[0])
        assertEquals(two, transactions[1])
    }
}