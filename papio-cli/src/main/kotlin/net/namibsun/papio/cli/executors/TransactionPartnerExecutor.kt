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
import net.sourceforge.argparse4j.ArgumentParsers

/**
 * Executor for the TransactionPartner root action
 * Handles the management of transaction partners in the database
 */
class TransactionPartnerExecutor : FullExecutor {

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeCreate(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli transactionpartner create")
                .build().defaultHelp(true)
        parser.addArgument("name").help("The name of the new transaction partner")
        val results = this.handleParserError(parser, args)
        val original = dbHandler.getTransactionPartner(results.getString("name"))
        val partner = dbHandler.createTransactionPartner(results.getString("name"))
        if (original == null) {
            println("Category created:\n$partner")
        } else {
            println("Category already exists:\n$partner")
        }
    }

    /**
     * Executes the 'delete' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDelete(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli transactionpartner delete")
                .build().defaultHelp(true)
        parser.addArgument("identifier").help("The name or ID of the transaction partner")
        val result = this.handleParserError(parser, args)

        val partner = this.getTransactionPartner(dbHandler, result.getString("identifier"))
        if (partner != null) {
            val confirm = this.getUserConfirmation(
                    "Delete transaction partner\n$partner\nand all transactions using it?"
            )
            if (confirm) {
                partner.delete(dbHandler)
                println("Transaction Partner has been deleted")
            } else {
                println("Deleting transaction partner cancelled")
            }
        } else {
            println("Transaction Partner not found.")
        }
    }

    /**
     * Executes the 'list' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeList(args: Array<String>, dbHandler: DbHandler) {
        for (partner in dbHandler.getTransactionPartners()) {
            println(partner)
        }
    }

    /**
     * Executes the 'display' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDisplay(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli transactionpartner display")
                .build().defaultHelp(true)
        parser.addArgument("identifier").help("The name or ID of the transaction partner")
        parser.addArgument("-t", "--transactions")
                .type(Int::class.java).setDefault(-1)
                .help("Sets the amount of transactions to display. By default, all transactions are displayed")
        val result = this.handleParserError(parser, args)

        val partner = this.getTransactionPartner(dbHandler, result.getString("identifier"))
        if (partner != null) {
            println("$partner\n")

            val transactions = partner.getAllTransactions(dbHandler)
            var limit = result.getInt("transactions")
            if (limit == -1 || limit > transactions.size) {
                limit = transactions.size
            }
            for (i in 0 until limit) {
                println(transactions[i])
            }
        } else {
            println("Transaction Partner not found")
        }
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