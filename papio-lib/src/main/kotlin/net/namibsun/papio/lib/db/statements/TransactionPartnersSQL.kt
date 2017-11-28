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
 * Class containing SQL statements managing transaction partners
 */
class TransactionPartnersSQL {

    companion object {

        /**
         * Deletes a transaction partner from the transaction partner table
         */
        val deleteTransactionPartner = "DELETE FROM transaction_partners WHERE transaction_partner_id=?"

        /**
         * Selects all transactions for a transaction partner
         */
        val getTransactionsByTransactionPartner= "SELECT * FROM transactions WHERE transaction_partner_id=?"
    }
}