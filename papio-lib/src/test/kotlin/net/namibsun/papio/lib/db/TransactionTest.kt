package net.namibsun.papio.lib.db

import net.namibsun.papio.lib.core.Currency
import net.namibsun.papio.lib.core.MoneyValue
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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
        this.wallet = this.handler!!.createWallet("Wallet", MoneyValue(100, Currency.EUR))
        this.category = this.handler!!.createCategory("Category")
        this.partner = this.handler!!.createTransactionPartner("Partner")
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
        val transaction = this.handler!!.createTransaction(
                this.wallet!!, this.category!!, this.partner!!,
                "Description", MoneyValue(100, Currency.EUR), 100
        )
        assertEquals(
                transaction,
                Transaction(
                        1, this.wallet!!, this.category!!, this.partner!!,
                        "Description", MoneyValue(100, Currency.EUR), 100
                )
        )
    }

    /**
     * Tests creating a transaction with a currency that differs from the wallet's currency
     * This should lead to the transaction's value being converted to the correct currency before storing
     */
    @Test
    fun testCreatingTransactionWithWrongCurrency() {
        val value = MoneyValue(100, Currency.USD)
        val transaction = this.handler!!.createTransaction(
                this.wallet!!, this.category!!, this.partner!!, "Description", value
        )
        assertEquals(transaction.getAmount().getCurrency(), Currency.EUR)
        assertNotEquals(transaction.getAmount(), value)
    }

    /**
     * Tests creating and subsequently deleting a transaction
     */
    @Test
    fun testDeletingTransaction() {
        val transaction = this.handler!!.createTransaction(
                this.wallet!!, this.category!!, this.partner!!,
                "Description", MoneyValue(100, Currency.EUR), 100
        )
        assertEquals(1, this.wallet!!.getAllTransactions(this.handler!!).size)
        transaction.delete(this.handler!!)
        assertEquals(0, this.wallet!!.getAllTransactions(this.handler!!).size)
    }

    /**
     * Tests creating two identical transactions. This should be possible, with both transactions being assigned
     * different IDs.
     */
    @Test
    fun testCreatingIdenticalTransactions() {
        val transactionOne = this.handler!!.createTransaction(
                this.wallet!!, this.category!!, this.partner!!,
                "Description", MoneyValue(100, Currency.EUR), 100
        )
        val transactionTwo = this.handler!!.createTransaction(
                this.wallet!!, this.category!!, this.partner!!,
                "Description", MoneyValue(100, Currency.EUR), 100
        )
        assertNotEquals(transactionOne, transactionTwo)
        assertNotEquals(transactionOne.id, transactionTwo.id)
        assertEquals(transactionOne.description, transactionTwo.description)
        assertEquals(transactionOne.unixUtcTimestamp, transactionTwo.unixUtcTimestamp)
        assertEquals(transactionOne.getAmount(), transactionTwo.getAmount())
    }

    /**
     * Tests retrieving all transactions for a wallet
     */
    @Test
    fun testGettingTransactionsForWallet() {
        val amount = MoneyValue(1, Currency.EUR)
        val newWallet = this.handler!!.createWallet("NewWallet", MoneyValue(0, Currency.EUR))
        for (i in 1..5) {
            this.handler!!.createTransaction(newWallet, this.category!!, this.partner!!, "$i", amount)
        }
        this.handler!!.createTransaction(this.wallet!!, this.category!!, this.partner!!, "A", amount)

        assertEquals(5, newWallet.getAllTransactions(this.handler!!).size)
        assertEquals(1, this.wallet!!.getAllTransactions(this.handler!!).size)

        val indices = mutableListOf(1, 2, 3, 4, 5)
        for (transaction in newWallet.getAllTransactions(this.handler!!)) {
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
        val amount = MoneyValue(1, Currency.EUR)
        val newCategory = this.handler!!.createCategory("NewCategory")
        for (i in 1..5) {
            this.handler!!.createTransaction(this.wallet!!, newCategory, this.partner!!, "$i", amount)
        }
        this.handler!!.createTransaction(this.wallet!!, this.category!!, this.partner!!, "A", amount)

        assertEquals(5, newCategory.getAllTransactions(this.handler!!).size)
        assertEquals(1, this.category!!.getAllTransactions(this.handler!!).size)

        val indices = mutableListOf(1, 2, 3, 4, 5)
        for (transaction in newCategory.getAllTransactions(this.handler!!)) {
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
        val amount = MoneyValue(1, Currency.EUR)
        val newPartner = this.handler!!.createTransactionPartner("NewPartner")
        for (i in 1..5) {
            this.handler!!.createTransaction(this.wallet!!, this.category!!, newPartner, "$i", amount)
        }
        this.handler!!.createTransaction(this.wallet!!, this.category!!, this.partner!!, "A", amount)

        assertEquals(5, newPartner.getAllTransactions(this.handler!!).size)
        assertEquals(1, this.partner!!.getAllTransactions(this.handler!!).size)

        val indices = mutableListOf(1, 2, 3, 4, 5)
        for (transaction in newPartner.getAllTransactions(this.handler!!)) {
            assertTrue(transaction.id in indices)
            indices.remove(transaction.id)
        }
        assertEquals(0, indices.size)
    }
}