/*
Copyright 2016 Hermann Krumrey <hermann@krumreyh.com>

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
import net.namibsun.papio.cli.ActionMode
import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.HelpException
import net.namibsun.papio.cli.execute
import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.CurrencyConverter
import net.namibsun.papio.lib.money.Value
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Class that tests the Transaction and Expense Executors
 */
class TransactionTester : TestHelper() {

    /**
     * The default wallet to test transactions
     */
    private val wallet = Wallet.create(this.dbHandler, "Wallet", Value("0", Currency.EUR))

    /**
     * The default category to test transactions
     */
    private val category = Category.create(this.dbHandler, "Category")

    /**
     * The default transaction partner to test transactions
     */
    private val partner = TransactionPartner.create(this.dbHandler, "Partner")

    /**
     * Tests creating a transaction with various options
     */
    @Test
    fun testCreatingTransaction() {

        // Explicit date
        execute(arrayOf("transaction", "create", "Wallet", "Category", "Partner", "Desc", "100.00", "-d", "2017-01-01"))
        assertEquals(Transaction.get(this.dbHandler, 1), Transaction(
                1, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR), IsoDate("2017-01-01")
        ))

        // Default date
        execute(arrayOf("transaction", "create", "Wallet", "Category", "Partner", "Desc", "100.00"))
        assertEquals(Transaction.get(this.dbHandler, 2), Transaction(
                2, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR), IsoDate()
        ))
    }

    /**
     * Tests if creating a transaction with a non-existing wallet, category or transaction partner is aborted
     */
    @Test
    fun testCreatingTransactionWithNotExistingDbValues() {
        for (command in listOf(
                arrayOf("transaction", "create", "Wallet1", "Category", "Partner", "Desc", "100.00"),
                arrayOf("transaction", "create", "Wallet", "Category1", "Partner", "Desc", "100.00"),
                arrayOf("transaction", "create", "Wallet", "Category", "Partner1", "Desc", "100.00")
        )) {
            try {
                execute(command)
                fail()
            } catch (e: AbortException) {
            }
        }
        assertEquals(0, Transaction.getAll(this.dbHandler).size)
    }

    /**
     * Tests creating wallets, categories and transaction partners via CLI parameters
     */
    @Test
    fun testGeneratingDbValuesOnTheFly() {
        for ((index, command) in listOf(
                arrayOf("transaction", "create", "Wallet1", "Category", "Partner", "Desc", "100.00",
                        "--create-wallet"),
                arrayOf("transaction", "create", "Wallet2", "Category", "Partner", "Desc", "100.00", "--create-wallet",
                        "--create-wallet-initial-value", "100.00", "--create-wallet-currency", "USD"),
                arrayOf("transaction", "create", "Wallet", "Category1", "Partner", "Desc", "100.00",
                        "--create-category"),
                arrayOf("transaction", "create", "Wallet", "Category", "Partner1", "Desc", "100.00",
                        "--create-transactionpartner")
        ).withIndex()) {
            execute(command)
            assertNotNull(Transaction.get(this.dbHandler, index + 1))
        }
        assertNotNull(Wallet.get(this.dbHandler, "Wallet1"))
        assertNotNull(Wallet.get(this.dbHandler, "Wallet2"))
        assertNotNull(Category.get(this.dbHandler, "Category1"))
        assertNotNull(TransactionPartner.get(this.dbHandler, "Partner1"))

        assertEquals(
                Wallet.get(this.dbHandler, "Wallet1"),
                Wallet(2, "Wallet1", Value("0.00", Currency.EUR))
        )
        assertEquals(
                Wallet.get(this.dbHandler, "Wallet2"),
                Wallet(3, "Wallet2", Value("100.00", Currency.USD))
        )
    }

    /**
     * Tests creating a transaction with invalid monetary and date values values
     */
    @Test
    fun testCreatingTransactionWithInvalidValues() {
        for (command in listOf(
                arrayOf("transaction", "create", "Wallet", "Category", "Partner", "Desc", "1a"),
                arrayOf("transaction", "create", "Wallet1", "Category", "Partner", "Desc", "100.00",
                        "--create-wallet", "--create-wallet-initial-value", "ABC"),
                arrayOf("transaction", "create", "Wallet", "Category", "Partner", "Desc", "100.00", "-d", "Hello")
        )) {
            try {
                execute(command)
                fail()
            } catch (e: AbortException) {
            }
        }
        assertEquals(0, Transaction.getAll(this.dbHandler).size)
    }

    /**
     * Tests creating an expense transaction
     */
    @Test
    fun testCreatingExpense() {
        execute(arrayOf("expense", "create", "Wallet", "Category", "Partner", "Desc", "100.00"))
        val expense = Transaction.get(this.dbHandler, 1)!!
        assertEquals(expense.getAmount(), Value("-100.00", Currency.EUR))
    }

    /**
     * Tests deleting a transaction
     */
    @Test
    fun testDeletingTransaction() {
        val transaction = Transaction.create(
                this.dbHandler, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR)
        )

        assertNotNull(Transaction.get(this.dbHandler, transaction.id))
        execute(arrayOf("transaction", "delete", transaction.id.toString()))
        assertNull(Transaction.get(this.dbHandler, transaction.id))
    }

    /**
     * Tests the error message that should be shown when trying to delete a transaction that does not exist
     */
    @Test
    fun testDeletingTransactionThatDoesNotExist() {
        assertNull(Transaction.get(this.dbHandler, 1))
        try {
            execute(arrayOf("transaction", "delete", "1"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Transaction 1 does not exist", e.message)
        }
    }

    /**
     * Tests cancelling a deletion of a transaction
     */
    @Test
    fun testCancellingDeleteTransaction() {
        Config.autoResponse = "n"
        val transaction = Transaction.create(
                this.dbHandler, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR)
        )
        assertNotNull(Transaction.get(this.dbHandler, transaction.id))
        execute(arrayOf("transaction", "delete", transaction.id.toString()))
        assertNotNull(Transaction.get(this.dbHandler, transaction.id))

        assertEquals("Transaction deletion cancelled\n", this.out.toString())
        Config.autoResponse = "y"
    }

    /**
     * Tests listing all transactions
     */
    @Test
    fun testListingTransactions() {
        val one = Transaction.create(
                this.dbHandler, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR), IsoDate("2017-01-01")
        )
        val two = Transaction.create(
                this.dbHandler, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR), IsoDate("1970-01-01")
        )
        val three = Transaction.create(
                this.dbHandler, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR), IsoDate("2000-01-01")
        )
        assertEquals(3, Transaction.getAll(this.dbHandler).size)
        execute(arrayOf("transaction", "list"))
        assertEquals("$one\n$three\n$two\n", this.out.toString())
    }

    /**
     * Tests displaying a transaction
     */
    @Test
    fun testDisplayingTransaction() {
        val transaction = Transaction.create(
                this.dbHandler, this.wallet, this.category, this.partner,
                "Desc", Value("100.00", Currency.EUR)
        )
        execute(arrayOf("transaction", "display", transaction.id.toString()))
        assertEquals("$transaction\n", this.out.toString())
    }

    /**
     * Tests the error message shown when trying to diplay a transaction that does not exist
     */
    @Test
    fun testDisplayingTransactionThatDoesNotExist() {
        assertNull(Transaction.get(this.dbHandler, 1))
        try {
            execute(arrayOf("transaction", "display", "1"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Transaction 1 does not exist", e.message)
        }
    }

    /**
     * Tests using an invalid action mode for the expense executor
     */
    @Test
    fun testUsingInvalidSubcommandForExpenseTransaction() {

        for (mode in ActionMode.values().filter { it != ActionMode.CREATE }) {
            try {
                execute(arrayOf("expense", mode.name))
                fail()
            } catch (e: HelpException) {
            }
        }
    }

    /**
     * Tests transferring a monetary amount from one wallet to another
     */
    @Test
    fun testTransferringBalanceFromOneWalletToAnother() {

        CurrencyConverter.update()

        val secondWallet = Wallet.create(this.dbHandler, "Second", Value("100", Currency.USD))
        assertEquals(Value("0", Currency.EUR), this.wallet.getBalance(this.dbHandler))
        assertEquals(Value("100", Currency.USD), secondWallet.getBalance(this.dbHandler))

        assertNull(TransactionPartner.get(this.dbHandler, this.wallet.name))
        assertNull(TransactionPartner.get(this.dbHandler, secondWallet.name))
        assertNull(Category.get(this.dbHandler, "Transfer"))

        execute(arrayOf("transfer", "Second", "Wallet", "50"))

        assertNotNull(TransactionPartner.get(this.dbHandler, this.wallet.name))
        assertNotNull(TransactionPartner.get(this.dbHandler, secondWallet.name))
        assertNotNull(Category.get(this.dbHandler, "Transfer"))

        assertTrue(this.wallet.getBalance(this.dbHandler).value > Value("0", Currency.EUR).value)
        assertEquals(Value("50", Currency.USD), secondWallet.getBalance(this.dbHandler))

        val transactionOne = Transaction.get(this.dbHandler, 1)!!
        val transactionTwo = Transaction.get(this.dbHandler, 2)!!

        assertEquals(transactionOne.getAmount(), Value("-50", Currency.USD))
        assertEquals(transactionTwo.getAmount(), Value("50", Currency.USD).convert(Currency.EUR))
        assertEquals(transactionOne.partner, TransactionPartner.get(this.dbHandler, this.wallet.name))
        assertEquals(transactionTwo.partner, TransactionPartner.get(this.dbHandler, secondWallet.name))
        assertEquals(transactionOne.category, Category.get(this.dbHandler, "Transfer"))
        assertEquals(transactionTwo.category, Category.get(this.dbHandler, "Transfer"))
        assertEquals(transactionOne.date, IsoDate())
        assertEquals(transactionTwo.date, IsoDate())

        execute(arrayOf("transfer", "Second", "Wallet", "50", "-d", "1970-01-01"))
        assertEquals(4, Transaction.getAll(this.dbHandler).size)
        assertEquals(
                Value("100", Currency.USD).convert(Currency.EUR).toString(),
                this.wallet.getBalance(this.dbHandler).toString()
        )
        assertEquals(Value("0", Currency.USD), secondWallet.getBalance(this.dbHandler))
        assertEquals(IsoDate("1970-01-01"), Transaction.get(this.dbHandler, 3)!!.date)
        assertEquals(IsoDate("1970-01-01"), Transaction.get(this.dbHandler, 4)!!.date)
    }

    /**
     * Tests transferring money to and from a wallet that does not exist
     */
    @Test
    fun testTransferringToAndFromWalletThatDoesNotExist() {
        assertNull(Wallet.get(this.dbHandler, "NotExisting"))

        for (command in listOf(
                arrayOf("transfer", "Wallet", "NotExisting", "100"),
                arrayOf("transfer", "NotExisting", "Wallet", "100")
        )) {
            try {
                execute(command)
                fail()
            } catch (e: AbortException) {
                assertEquals("Wallet NotExisting does not exist", e.message)
            }
        }
    }

    /**
     * Tests triggering the transfer executor's help message
     */
    @Test
    fun testTriggeringHelpMessage() {

        for (mode in ActionMode.values().map { it.name }) {
            try {
                execute(arrayOf("transfer", mode))
                fail()
            } catch (e: HelpException) {}
        }

        for (command in listOf(
                arrayOf("transfer"),
                arrayOf("transfer", "test"),
                arrayOf("transfer", "test", "test")
        )) {
            try {
                execute(command)
                fail()
            } catch (e: AbortException) {}
        }
    }

    /**
     * Tests creating a money transfer with an invalid amount
     */
    @Test
    fun testTransferringInvalidMonetaryAmount() {
        try {
            execute(arrayOf("transfer", "Wallet", "Wallet", "10::10"))
            fail()
        } catch (e: AbortException) {
            assertEquals("10::10 is not a valid monetary value", e.message)
        }
    }
}