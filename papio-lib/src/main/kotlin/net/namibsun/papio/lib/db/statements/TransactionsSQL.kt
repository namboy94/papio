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

package net.namibsun.papio.lib.db.statements

/**
 * Class containing Transaction-related SQL statements
 */
class TransactionsSQL {

    companion object {

        /**
         * Retrieves all transactions for a wallet
         */
        val getTransactionsForWallet = "" +
                "SELECT * " +
                "FROM transactions " +
                "WHERE wallet_id=?"

        /**
         * Stores a new transaction
         */
        val storeTransaction = "" +
                "INSERT INTO transactions " +
                "(wallet_id, category_id, transaction_partner_id, description, amount, unix_utc_timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?)"

        /**
         * Adjusts the amount of a transaction
         */
        val adjustTransactionAmount = "UPDATE transactions SET amount=? WHERE id=?"

        /**
         * Deletes a transaction
         */
        val deleteTransaction = "DELETE FROM transactions WHERE id=?"

    }

}
