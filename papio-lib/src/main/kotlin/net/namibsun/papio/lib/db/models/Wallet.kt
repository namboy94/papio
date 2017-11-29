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

package net.namibsun.papio.lib.db.models
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.MoneyValue
import net.namibsun.papio.lib.db.DbHandler

data class Wallet(val id: Int, val name: String, private var startingValue: MoneyValue) {

    /**
     * Calculates the balance of all transactions
     * @param dbHandler: The database handler to use
     * @return The current balance of the wallet
     */
    fun getBalance(dbHandler: DbHandler): MoneyValue {
        var value = MoneyValue(0, this.startingValue.getCurrency())
        for (transaction in this.getAllTransactions(dbHandler)) {
            value += transaction.getAmount()
        }
        return value + this.startingValue
    }

    /**
     * Retrieves the currency currently used within this wallet
     * @return The currently used currency of the wallet
     */
    fun getCurrency(): Currency {
        return this.startingValue.getCurrency()
    }

    /**
     * Retrieves all transactions in a wallet
     * @param dbHandler: The database handler to use
     * @return A list of transactions present in this wallet
     */
    fun getAllTransactions(dbHandler: DbHandler): List<Transaction> {
        return dbHandler.getTransactionsByWallet(this.id)
    }

    /**
     * Converts the currency of the wallet. All transaction values in the wallet will also be converted.
     */
    fun convertCurrency(dbHandler: DbHandler, currency: Currency) {
        for (transaction in this.getAllTransactions(dbHandler)) {
            transaction.convertCurrency(dbHandler, currency)
        }
        // Change currency of transaction BEFORE the wallet itself!
        this.startingValue = this.startingValue.convert(currency)
        dbHandler.adjustWalletStartingValue(this.id, this.startingValue)
    }

    /**
     * Deletes this wallet and all associated transactions
     * @param dbHandler: The database handler to use
     */
    fun delete(dbHandler: DbHandler) {
        dbHandler.deleteWallet(this.id)
    }
}