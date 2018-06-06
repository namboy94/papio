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
import net.namibsun.papio.cli.execute
import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.CurrencyConverter
import net.namibsun.papio.lib.money.Value
import org.junit.After
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

/**
 * Class that tests the TransactionPartnerExecutor class
 */
class TransactionPartnerTester : TestHelper() {

    /**
     * Deletes the temporary config files
     */
    @After
    fun deleteConfig() {
        this.dbHandler.close()
        Config.papioPath.deleteRecursively()
    }

    /**
     * Tests creating a transaction partner
     */
    @Test
    fun testCreatingTransactionPartner() {
        assertNull(TransactionPartner.get(this.dbHandler, "Test"))
        execute(arrayOf("transactionpartner", "create", "Test"))

        val partner = TransactionPartner.get(this.dbHandler, "Test")
        assertNotNull(partner)

        assertEquals("Transaction Partner created:\n$partner\n", this.out.toString())
    }

    /**
     * Tests creating a transaction partner that already exists
     */
    @Test
    fun testCreatingTransactionPartnerAgain() {

        assertNull(TransactionPartner.get(this.dbHandler, "Test"))

        execute(arrayOf("transactionpartner", "create", "Test"))
        val partnerOne = TransactionPartner.get(this.dbHandler, "Test")
        assertNotNull(partnerOne)

        this.out = ByteArrayOutputStream()
        System.setOut(PrintStream(this.out))

        try {
            execute(arrayOf("transactionpartner", "create", "Test"))
            fail()
        } catch (e: AbortException) {
            val partnerTwo = TransactionPartner.get(this.dbHandler, "Test")
            assertNotNull(partnerTwo)
            assertEquals(partnerOne, partnerTwo)
            assertEquals("Transaction Partner already exists:\n$partnerOne", e.message)
        }
    }

    /**
     * Tests listing all transaction partners in the database
     */
    @Test
    fun testListingCategories() {
        val one = TransactionPartner.create(this.dbHandler, "One")
        val two = TransactionPartner.create(this.dbHandler, "Two")
        val three = TransactionPartner.create(this.dbHandler, "Three")
        execute(arrayOf("transactionpartner", "list"))
        assertEquals("$one\n$two\n$three\n", this.out.toString())
    }

    /**
     * Tests displaying a partner with both an ID and a name
     */
    @Test
    fun testDisplayingTransactionPartner() {

        CurrencyConverter.update()

        val partnerData = this.initializeDisplayableTransactionPartner()
        val partner = partnerData.first
        val transactionOne = partnerData.second
        val transactionTwo = partnerData.third

        execute(arrayOf("transactionpartner", "display", partner.name))
        print("SPLIT")
        execute(arrayOf("transactionpartner", "display", partner.id.toString()))

        for (result in this.out.toString().split("SPLIT")) {
            assertEquals("$partner\n\n$transactionTwo\n$transactionOne\n", result)
        }
    }

    /**
     * Tests limiting the amount of transactions to display
     */
    @Test
    fun testDisplayingTransactionPartnerWithLimitedTransactions() {

        CurrencyConverter.update()

        val partnerData = this.initializeDisplayableTransactionPartner()
        val partner = partnerData.first
        val transactionOne = partnerData.second
        val transactionTwo = partnerData.third

        execute(arrayOf("transactionpartner", "display", partner.name, "-t", "500"))
        print("SPLIT")
        execute(arrayOf("transactionpartner", "display", partner.name, "-t", "2"))
        print("SPLIT")
        execute(arrayOf("transactionpartner", "display", partner.name, "-t", "1"))
        print("SPLIT")
        execute(arrayOf("transactionpartner", "display", partner.name, "-t", "-1"))
        print("SPLIT")
        execute(arrayOf("transactionpartner", "display", partner.name, "-t", "-1000"))

        val outData = this.out.toString().split("SPLIT")
        assertEquals("$partner\n\n$transactionTwo\n$transactionOne\n", outData[0])
        assertEquals("$partner\n\n$transactionTwo\n$transactionOne\n", outData[1])
        assertEquals("$partner\n\n$transactionTwo\n", outData[2])
        assertEquals("$partner\n\n$transactionTwo\n$transactionOne\n", outData[3])
        assertEquals("$partner\n\n", outData[4])
    }

    /**
     * Tests displaying a transaction partner that does not exist
     */
    @Test
    fun testDisplayingTransactionPartnerIfTransactionPartnerDoesNotExist() {
        try {
            execute(arrayOf("transactionpartner", "display", "One"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Transaction Partner One does not exist", e.message)
        }
    }

    /**
     * Tests deleting a transaction partner with both an ID or a name
     */
    @Test
    fun testDeletingTransactionPartner() {
        val one = TransactionPartner.create(this.dbHandler, "One")
        val two = TransactionPartner.create(this.dbHandler, "Two")

        assertNotNull(TransactionPartner.get(this.dbHandler, one.id))
        assertNotNull(TransactionPartner.get(this.dbHandler, two.id))

        execute(arrayOf("transactionpartner", "delete", "One"))
        assertNull(TransactionPartner.get(this.dbHandler, one.id))
        assertNotNull(TransactionPartner.get(this.dbHandler, two.id))

        execute(arrayOf("transactionpartner", "delete", "2"))
        assertNull(TransactionPartner.get(this.dbHandler, one.id))
        assertNull(TransactionPartner.get(this.dbHandler, two.id))
    }

    /**
     * Tests cancelling a delete operation
     */
    @Test
    fun testCancellingDeletingTransactionPartner() {
        val partner = TransactionPartner.create(this.dbHandler, "One")
        Config.autoResponse = "n"

        assertNotNull(TransactionPartner.get(this.dbHandler, partner.id))
        execute(arrayOf("transactionpartner", "delete", "One"))
        assertNotNull(TransactionPartner.get(this.dbHandler, partner.id))

        Config.autoResponse = "y"
    }

    /**
     * Tests selecting a transaction partner that does not exist
     */
    @Test
    fun testDeletingTransactionPartnerThatDoesNotExist() {
        try {
            execute(arrayOf("transactionpartner", "delete", "One"))
            fail()
        } catch (e: AbortException) {
            assertEquals("Transaction Partner One does not exist", e.message)
        }
    }

    /**
     * Initializes a transaction partner with two transactions
     * @return A triple of the partner and its transactions
     */
    private fun initializeDisplayableTransactionPartner(): Triple<TransactionPartner, Transaction, Transaction> {
        val partner = TransactionPartner.create(this.dbHandler, "One")

        val wallet = Wallet.create(this.dbHandler, "Wallet", Value("0", Currency.EUR))
        val category = Category.create(this.dbHandler, "Category")
        val transactionOne = Transaction.create(
                this.dbHandler, wallet, category, partner,
                "Desc1", Value("1", Currency.EUR), IsoDate("2000-01-01")
        )
        val transactionTwo = Transaction.create(
                this.dbHandler, wallet, category, partner,
                "Desc2", Value("2", Currency.USD), IsoDate("2017-01-01")
        )
        return Triple(partner, transactionOne, transactionTwo)
    }
}