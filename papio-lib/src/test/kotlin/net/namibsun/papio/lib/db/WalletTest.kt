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
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.Value
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.math.BigDecimal
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
        val wallet = Wallet.create(this.handler!!, "Wallet", Value("100", Currency.EUR))
        assertEquals(wallet, Wallet(1, "Wallet", Value("100", Currency.EUR)))
        assertEquals(wallet, Wallet.get(this.handler!!, "Wallet"))
        assertEquals(wallet, Wallet.get(this.handler!!, 1))
    }

    /**
     * Tests creating a wallet of the same name as an existing wallet.
     * The second wallet should not be accepted and instead the original wallet should be returned
     */
    @Test
    fun testCreatingDuplicateWallet() {
        val original = Wallet.create(this.handler!!, "Wallet", Value("100", Currency.EUR))
        val new = Wallet.create(this.handler!!, "Wallet", Value("200", Currency.USD))
        assertEquals(original, new)
        assertEquals(original, Wallet(1, "Wallet", Value("100", Currency.EUR)))
    }

    /**
     * Tests deleting a wallet from the database
     */
    @Test
    fun testDeletingWallet() {
        val original = Wallet.create(this.handler!!, "Wallet", Value("100", Currency.EUR))
        assertNotNull(Wallet.get(this.handler!!, "Wallet"))
        original.delete(this.handler!!)
        assertNull(Wallet.get(this.handler!!, "Wallet"))
    }

    /**
     * Tests changing the currency of a wallet. Should propagate through all transactions and change their values too.
     */
    @Test
    fun testChangingCurrency() {
        val wallet = Wallet.create(this.handler!!, "Wallet", Value("100", Currency.EUR))
        val category = Category.create(this.handler!!, "Category")
        val partner = TransactionPartner.create(this.handler!!, "Partner")

        val transactionOne = Transaction.create(this.handler!!, 
                wallet, category, partner, "One", Value("1000", Currency.EUR)
        )
        val transactionTwo = Transaction.create(this.handler!!, 
                wallet, category, partner, "Two", Value("-500", Currency.EUR)
        )

        val oldBalance = wallet.getBalance(this.handler!!)

        wallet.convertCurrency(this.handler!!, Currency.USD)

        val dbWallet = Wallet.get(this.handler!!, "Wallet")!!
        assertEquals(wallet.getCurrency(), dbWallet.getCurrency())
        assertEquals(wallet.getCurrency(), Currency.USD)
        assertEquals(wallet.getBalance(this.handler!!), dbWallet.getBalance(this.handler!!))
        assertNotEquals(wallet.getBalance(this.handler!!), oldBalance)

        for (transaction in wallet.getTransactions(this.handler!!)) {
            assertNotEquals(transaction, transactionOne)
            assertNotEquals(transaction, transactionTwo)
            assertEquals(transaction.getAmount().currency, Currency.USD)
            assertNotEquals(transaction.getAmount().value, BigDecimal("1000"))
            assertNotEquals(transaction.getAmount().value, BigDecimal("-500"))
        }
    }

    /**
     * Tests calculating the balance of a wallet with multiple transactions
     */
    @Test
    fun testBalanceCalculation() {

        val wallet = Wallet.create(this.handler!!, "Wallet", Value("100", Currency.EUR))
        val cat = Category.create(this.handler!!, "Category")
        val part = TransactionPartner.create(this.handler!!, "Partner")

        Transaction.create(this.handler!!, wallet, cat, part, "One", Value("1000", Currency.EUR))
        Transaction.create(this.handler!!, wallet, cat, part, "Two", Value("-50", Currency.EUR))

        val balance = wallet.getBalance(this.handler!!)
        assertEquals(balance.value, BigDecimal("1050"))
        assertEquals(balance.currency, Currency.EUR)
        assertEquals(balance, Value("1050", Currency.EUR))
    }

    /**
     * Tests retrieving all wallets from the database
     */
    @Test
    fun testGettingWallets() {
        val walletOne = Wallet.create(this.handler!!, "Wallet1", Value("100", Currency.EUR))
        val walletTwo = Wallet.create(this.handler!!, "Wallet2", Value("200", Currency.EUR))
        val walletThree = Wallet.create(this.handler!!, "Wallet3", Value("300", Currency.EUR))

        val wallets = Wallet.getAll(this.handler!!)

        assertEquals(3, wallets.size)
        for (wallet in wallets) {
            for (original in listOf(walletOne, walletTwo, walletThree)) {
                if (wallet.name == original.name) {
                    assertEquals(wallet, original)
                } else {
                    assertNotEquals(wallet, original)
                }
            }
        }
    }

    /**
     * Tests the toString method of the Wallet class
     */
    @Test
    fun testStringRepresentation() {
        val wallet = Wallet.create(this.handler!!, "A", Value("100", Currency.EUR))
        val category = Category.create(this.handler!!, "B")
        val partner = TransactionPartner.create(this.handler!!, "C")
        Transaction.create(this.handler!!, 
                wallet, category, partner, "D", Value("500", Currency.EUR)
        )

        assertEquals("Wallet; ID: 1; Name: A; Starting Value: EUR 1.00", wallet.toString())
        assertEquals(
                "Wallet; ID: 1; Name: A; Balance: EUR 6.00; Starting Value: EUR 1.00",
                wallet.toString(this.handler!!)
        )
    }
}
