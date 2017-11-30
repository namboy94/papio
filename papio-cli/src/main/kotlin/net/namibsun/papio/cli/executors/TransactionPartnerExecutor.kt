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

import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.models.TransactionPartner

/**
 * Executor for the TransactionPartner root action
 * Handles the management of transaction partners in the database
 */
class TransactionPartnerExecutor : Executor {

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeCreate(args: Array<String>, dbHandler: DbHandler) {
    }

    /**
     * Executes the 'delete' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDelete(args: Array<String>, dbHandler: DbHandler) {
    }

    /**
     * Executes the 'list' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeList(args: Array<String>, dbHandler: DbHandler) {
    }

    /**
     * Executes the 'display' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDisplay(args: Array<String>, dbHandler: DbHandler) {
    }

    /**
     * Tries to retrieve a transaction partner based on the transaction partner's name or ID, in that order.
     * @param dbHandler: The Database handler to use
     * @param nameOrId: The identifier to use to find the transaction partner
     * @return The retrieved transaction partner or null if none was found
     */
    fun getTransactionPartner(dbHandler: DbHandler, nameOrId: String): TransactionPartner? {
        var partner = dbHandler.getTransactionPartner(nameOrId)
        if (partner == null) {
            try {
                partner = dbHandler.getTransactionPartner(nameOrId.toInt())
            } catch (e: NumberFormatException) {}
        }
        return partner
    }
}