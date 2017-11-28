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

import net.namibsun.papio.lib.core.Currency
import net.namibsun.papio.lib.core.MoneyValue
import net.namibsun.papio.lib.db.DbHandler

/**
 * Models a transaction in the database
 * A transaction always references a wallet, a category and a transaction partner
 * Transactions are immutable and have to be deleted and created anew to be changed.
 * An exception to this is the amount, which may be changed when the associated wallet
 * changes its currency.
 * @param id: The transaction's database ID
 * @param wallet: The wallet associated with this transaction
 * @param category: The category associated with this transaction
 * @param partner: The partner of this transaction
 * @param description: A description of the transaction
 * @param amount: The amount of money that makes up the transaction. Always the same currency as the wallet
 * @param unixUtcTimestamp: The timestamp indicating when the transaction took place
 */
data class Transaction(val id: Int,
                       private val wallet: Wallet,
                       private val category: Category,
                       private val partner: TransactionPartner,
                       val description: String,
                       private var amount: MoneyValue,
                       val unixUtcTimestamp: Int) {

    /**
     * Retrieves the amount of money in this transaction
     * @return The amount of money in this transaction
     */
    fun getAmount(): MoneyValue {
        return this.amount
    }

    /**
     * Deletes a transaction
     * @param dbHandler: The Database Handler to use
     */
    fun delete(dbHandler: DbHandler) {
        dbHandler.deleteTransaction(this.id)
    }

    /**
     * Converts the currency of a transaction
     * @param dbHandler: The Database Handler to use
     * @param currency: The currency to use
     */
    fun convertCurrency(dbHandler: DbHandler, currency: Currency) {
        this.amount.convert(currency)
        dbHandler.adjustTransactionAmount(this.id, this.amount.getValue())
    }

}