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

import net.namibsun.papio.lib.db.DbHandler

/**
 * Class that models a transaction partner in the database
 * @param id: The ID of the transaction partner
 * @param name: The name of the transaction partner
 */
data class TransactionPartner(val id: Int, val name: String) {

    /**
     * Deletes the Transaction Partner from the database
     * @param dbHandler: The database handler to use
     */
    fun delete(dbHandler: DbHandler) {
        dbHandler.deleteTransactionPartner(this.id)
    }

    /**
     * Retrieves all transactions with this transaction partner
     * @param dbHandler: The Database Handler to use
     * @return The list of transactions
     */
    fun getAllTransactions(dbHandler: DbHandler): List<Transaction> {
        return dbHandler.getTransactionsByTransactionPartner(this.id)
    }
}