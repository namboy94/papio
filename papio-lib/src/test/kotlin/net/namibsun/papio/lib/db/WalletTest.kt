package net.namibsun.papio.lib.db

import net.namibsun.papio.lib.core.Currency
import net.namibsun.papio.lib.core.MoneyValue
import net.namibsun.papio.lib.db.models.Wallet
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
 * Class that tests the Wallet class and related DbHandler functions
 */
class WalletTest {

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
     * Tests creating a wallet and retrieving it afterwards
     */
    @Test
    fun testCreatingAndGettingWallet() {
        val wallet = this.handler!!.createWallet("Wallet", MoneyValue(100, Currency.EUR))
        assertEquals(wallet, Wallet(1, "Wallet", MoneyValue(100, Currency.EUR)))
        assertEquals(wallet, this.handler!!.getWallet("Wallet"))
        assertEquals(wallet, this.handler!!.getWallet(1))
    }

    /**
     * Tests creating a wallet of the same name as an existing wallet.
     * The second wallet should not be accepted and instead the original wallet should be returned
     */
    @Test
    fun testCreatingDuplicateWallet() {
        val original = this.handler!!.createWallet("Wallet", MoneyValue(100, Currency.EUR))
        val new = this.handler!!.createWallet("Wallet", MoneyValue(200, Currency.USD))
        assertEquals(original, new)
        assertEquals(original, Wallet(1, "Wallet", MoneyValue(100, Currency.EUR)))
    }

    /**
     * Tests deleting a wallet from the database
     */
    @Test
    fun testDeletingWallet() {
        val original = this.handler!!.createWallet("Wallet", MoneyValue(100, Currency.EUR))
        assertNotNull(this.handler!!.getWallet("Wallet"))
        original.delete(this.handler!!)
        assertNull(this.handler!!.getWallet("Wallet"))
    }

    /**
     * Tests changing the currency of a wallet. Should propagate through all transactions and change their values too.
     */
    @Test
    fun testChangingCurrency() {
        val wallet = this.handler!!.createWallet("Wallet", MoneyValue(100, Currency.EUR))
        val category = this.handler!!.createCategory("Category")
        val partner = this.handler!!.createTransactionPartner("Partner")

        val transactionOne = this.handler!!.createTransaction(
                wallet, category, partner, "One", MoneyValue(1000, Currency.EUR)
        )
        val transactionTwo = this.handler!!.createTransaction(
                wallet, category, partner, "Two", MoneyValue(-500, Currency.EUR)
        )

        val oldBalance = wallet.getBalance(this.handler!!)

        wallet.convertCurrency(this.handler!!, Currency.USD)

        val dbWallet = this.handler!!.getWallet("Wallet")!!
        assertEquals(wallet.getCurrency(), dbWallet.getCurrency())
        assertEquals(wallet.getCurrency(), Currency.USD)
        assertEquals(wallet.getBalance(this.handler!!), dbWallet.getBalance(this.handler!!))
        assertNotEquals(wallet.getBalance(this.handler!!), oldBalance)

        for (transaction in wallet.getAllTransactions(this.handler!!)) {
            assertNotEquals(transaction, transactionOne)
            assertNotEquals(transaction, transactionTwo)
            assertEquals(transaction.getAmount().getCurrency(), Currency.USD)
            assertNotEquals(transaction.getAmount().getValue(), 1000)
            assertNotEquals(transaction.getAmount().getValue(), -500)
        }
    }

    /**
     * Tests calculating the balance of a wallet with multiple transactions
     */
    @Test
    fun testBalanceCalculation() {

        val wallet = this.handler!!.createWallet("Wallet", MoneyValue(100, Currency.EUR))
        val cat = this.handler!!.createCategory("Category")
        val part = this.handler!!.createTransactionPartner("Partner")

        this.handler!!.createTransaction(wallet, cat, part, "One", MoneyValue(1000, Currency.EUR))
        this.handler!!.createTransaction(wallet, cat, part, "Two", MoneyValue(-50, Currency.EUR))

        val balance = wallet.getBalance(this.handler!!)
        assertEquals(balance.getValue(), 1050)
        assertEquals(balance.getCurrency(), Currency.EUR)
        assertEquals(balance, MoneyValue(1050, Currency.EUR))
    }
}
