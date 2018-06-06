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

import net.namibsun.papio.cli.ActionMode
import net.namibsun.papio.cli.HelpException
import net.namibsun.papio.lib.db.DbHandler

/**
 * Class that extends the Transaction Executor with the ability to automatically create
 * expenses without having to manually set the amount to store as negative
 */
class ExpenseExecutor : TransactionExecutor() {

    /**
     * Makes sure that expenses can only be created
     * @param args: The command line arguments to parse
     * @param dbHandler: The database handler to use
     * @param mode: The mode for which to execute
     * @throws HelpException: If the user input is invalid and the root help message should be printed
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {
        when (mode) {
            ActionMode.CREATE -> { this.executeCreateExpense(args, dbHandler) }
            else -> { throw HelpException() }
        }
    }

    /**
     * Executes the 'create' option for an expense transaction
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    private fun executeCreateExpense(args: Array<String>, dbHandler: DbHandler) {
        this.createTransaction(args, dbHandler, true)
    }
}