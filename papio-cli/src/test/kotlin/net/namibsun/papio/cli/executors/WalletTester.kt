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

import net.namibsun.papio.cli.AbortException
import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.HelpException
import net.namibsun.papio.cli.execute
import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.Value
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

/**
 * Class that tests the wallet executor
 */
class WalletTester : TestHelper() {

    /**
     * Tests creating a wallet
     */
    @Test
    fun testWalletCreation() {

        assertEquals(0, Wallet.getAll(this.dbHandler).size)

        execute(arrayOf("wallet", "create", "Test1"))
        execute(arrayOf("wallet", "create", "Test2", "--initial-value", "100.00"))
        execute(arrayOf("wallet", "create", "Test3", "--initial-value", "100.00", "--currency", "USD"))

        assertEquals(
                Wallet(1, "Test1", Value("0", Currency.EUR)),
                Wallet.get(this.dbHandler, "Test1")
        )
        assertEquals(
                Wallet(2, "Test2", Value("100", Currency.EUR)),
                Wallet.get(this.dbHandler, "Test2")
        )
        assertEquals(
                Wallet(3, "Test3", Value("100", Currency.USD)),
                Wallet.get(this.dbHandler, "Test3")
        )
    }

    /**
     * Tests creating a wallet with an invalid initial value
     */
    @Test
    fun testCreatingWalletWithInvalidInitialValue() {
        try {
            execute(arrayOf("wallet", "create", "Test1", "--initial-value", "AAA"))
            fail()
        } catch (e: AbortException) {
            assertEquals("AAA is not a valid monetary value", e.message)
        }
    }

    /**
     * Tests creating a wallet using an invalid currency
     */
    @Test
    fun testCreatingWalletWithInvalidCurrency() {
        try {
            execute(arrayOf("wallet", "create", "Test1", "--currency", "AAA"))
            fail()
        } catch (e: AbortException) { } // Handled by argparse4j
    }

    /**
     * Tests creating a wallet that already exists
     */
    @Test
    fun testCreatingWalletThatAlreadyExists() {
        val wallet = Wallet.create(this.dbHandler, "Test", Value("100", Currency.EUR))

        try {
            execute(arrayOf("wallet", "create", "Test"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Wallet\n$wallet\nalready exists", e.message)
        }
    }

    /**
     * Tests deleting a wallet
     */
    @Test
    fun testDeletingWallet() {
        assertNotNull(Wallet.create(this.dbHandler, "Test1", Value("10000", Currency.BTC)))
        assertNotNull(Wallet.create(this.dbHandler, "Test2", Value("100", Currency.ZAR)))
        assertNotNull(Wallet.get(this.dbHandler, 1))
        assertNotNull(Wallet.get(this.dbHandler, 2))
        execute(arrayOf("wallet", "delete", "1"))
        assertNull(Wallet.get(this.dbHandler, 1))
        execute(arrayOf("wallet", "delete", "Test2"))
        assertNull(Wallet.get(this.dbHandler, 2))
    }

    /**
     * Tests deleting a wallet and cancelling the deletion process
     */
    @Test
    fun testCancellingDelete() {
        Config.autoResponse = "n"
        Wallet.create(this.dbHandler, "Wallet", Value("100", Currency.EUR))
        execute(arrayOf("wallet", "delete", "Wallet"))
        assertEquals("Deleting wallet cancelled\n", this.out.toString())
        Config.autoResponse = "y"
    }

    /**
     * Tests deleting a wallet that does not currently exist in the database
     */
    @Test
    fun testDeletingWalletThatDoesNotExist() {
        assertNull(Wallet.get(this.dbHandler, "Test"))
        try {
            execute(arrayOf("wallet", "delete", "Test"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Wallet Test does not exist", e.message)
        }
    }

    /**
     * Tests listing all wallets in the database
     */
    @Test
    fun testListingWallets() {
        val one = Wallet.create(this.dbHandler, "Test1", Value("0", Currency.EUR))
        val two = Wallet.create(this.dbHandler, "Test2", Value("0.12345", Currency.BTC))
        val three = Wallet.create(this.dbHandler, "Test3", Value("11111", Currency.USD))
        execute(arrayOf("wallet", "list"))
        val total = one.getBalance(this.dbHandler) + two.getBalance(this.dbHandler) + three.getBalance(this.dbHandler)
        assertEquals(
                "${one.toString(this.dbHandler)}\n${two.toString(this.dbHandler)}" +
                        "\n${three.toString(this.dbHandler)}\n-------------------------\n" +
                        "Total: $total\n",
                this.out.toString()
        )
    }

    /**
     * Test displaying a wallet
     */
    @Test
    fun testDisplayingWallet() {

        val initial = this.initializeTransactions()
        val wallet = initial.first
        val one = initial.second
        val two = initial.third
        execute(arrayOf("wallet", "display", "1"))
        print("SPLIT")
        execute(arrayOf("wallet", "display", "Test"))

        for (output in this.out.toString().split("SPLIT")) {
            assertEquals("${wallet.toString(this.dbHandler)}\n\n$two\n$one\n", output)
        }
    }

    /**
     * Tests displaying a wallet that does not exist
     */
    @Test
    fun testDisplayingWalletThatDoesNotExist() {
        try {
            execute(arrayOf("wallet", "display", "Test"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Wallet Test does not exist", e.message)
        }
    }

    /**
     * Tests displaying a wallet with various parameters for the amount of transactions to display
     */
    @Test
    fun testDisplayingWalletWithLimitedTransactionCount() {
        val initial = this.initializeTransactions()
        val wallet = initial.first
        val one = initial.second
        val two = initial.third

        execute(arrayOf("wallet", "display", "1", "-t", "2"))
        print("SPLIT")
        execute(arrayOf("wallet", "display", "1", "-t", "-1"))
        print("SPLIT")
        execute(arrayOf("wallet", "display", "1", "-t", "100"))
        print("SPLIT")
        execute(arrayOf("wallet", "display", "1", "-t", "-100"))
        print("SPLIT")
        execute(arrayOf("wallet", "display", "1", "-t", "1"))
        print("SPLIT")
        execute(arrayOf("wallet", "display", "1", "-t", "0"))

        val results = this.out.toString().split("SPLIT")
        val walletString = wallet.toString(this.dbHandler)

        assertEquals("$walletString\n\n$two\n$one\n", results[0])
        assertEquals("$walletString\n\n$two\n$one\n", results[1])
        assertEquals("$walletString\n\n$two\n$one\n", results[2])
        assertEquals("$walletString\n\n", results[3])
        assertEquals("$walletString\n\n$two\n", results[4])
        assertEquals("$walletString\n\n", results[5])
    }

    /**
     * Initializes a wallet and two transactions
     * @return A triple of wallet, transaction one, transaction three
     */
    private fun initializeTransactions(): Triple<Wallet, Transaction, Transaction> {
        val wallet = Wallet.create(this.dbHandler, "Test", Value("100", Currency.USD))
        val category = Category.create(this.dbHandler, "Category")
        val partner = TransactionPartner.create(this.dbHandler, "Partner")

        val transactionOne = Transaction.create(
                this.dbHandler, wallet, category, partner,
                "Desc1", Value("10", Currency.USD), IsoDate("1970-01-01")
        )
        val transactionTwo = Transaction.create(
                this.dbHandler, wallet, category, partner,
                "Desc2", Value("-90", Currency.USD), IsoDate("2000-01-01")
        )
        return Triple(wallet, transactionOne, transactionTwo)
    }

    /**
     * Tests what happens when null is passed as the wallet executor's action mode
     * Should throw a HelpException
     */
    @Test
    fun testExecutingWalletExecutorWithNullAsActionMode() {
        try {
            WalletExecutor().execute(arrayOf(), this.dbHandler, null)
            fail()
        } catch (e: HelpException) {}
    }
}