package net.namibsun.papio.lib.db

import net.namibsun.papio.lib.core.Currency
import net.namibsun.papio.lib.core.MoneyValue
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager

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

    }

    /**
     * Tests creating a transaction with a currency that differs from the wallet's currency
     * This should lead to the transaction's value being converted to the correct currency before storing
     */
    @Test
    fun testCreatingTransactionWithWrongCurrency() {

    }

    /**
     * Tests creating and subsequently deleting a transaction
     */
    @Test
    fun testDeletingTransaction() {

    }

    /**
     * Tests creating two identical transactions. This should be possible, with both transactions being assigned
     * different IDs.
     */
    @Test
    fun testCreatingIdenticalTransactions() {

    }

    /**
     * Tests retrieving all transactions for a wallet
     */
    @Test
    fun testGettingTransactionsForWallet() {

    }

    /**
     * Tests retrieving all transactions for a category
     */
    @Test
    fun testGettingTransactionsForCategory() {

    }

    /**
     * Tests retrieving all transactions for a transaction partner
     */
    @Test
    fun testGettingTransactionsForTransactionPartner() {

    }

}